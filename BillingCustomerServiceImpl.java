package com.services.billingservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.billingservice.dto.BillingMIDTO;
import com.services.billingservice.dto.customer.*;
import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.enums.ChangeAction;
import com.services.billingservice.exception.BadRequestException;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingCustomer;
import com.services.billingservice.model.BillingDataChange;
import com.services.billingservice.repository.BillingCustomerRepository;
import com.services.billingservice.repository.BillingDataChangeRepository;
import com.services.billingservice.service.BillingCustomerService;
import com.services.billingservice.service.BillingMIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingCustomerServiceImpl implements BillingCustomerService {

    // private static final String EMAIL_PATTERN = "^([^.@]+)(\.[^.@]+)*@([^.@]+\.)+([^.@]+)$";

    private final BillingCustomerRepository billingCustomerRepository;
    private final BillingMIService billingMIService;
    private final BillingDataChangeRepository billingDataChangeRepository;
    private final ObjectMapper objectMapper;

    @Override
    public BillingCustomerDTO createBillingCustomer(CreateBillingCustomerRequest request) {
        // get is data primary request
        String customerCode = request.getCustomerCode();
        String miCode = request.getMiCode();
        BigDecimal customerMinimumFee = request.getCustomerMinimumFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerMinimumFee());
        BigDecimal customerSafekeepingFee = request.getCustomerSafekeepingFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerSafekeepingFee());

        // TODO: check already Customer Code
        Boolean checkExistByCustomerCode = billingCustomerRepository.existsByCustomerCode(customerCode);
        if (checkExistByCustomerCode) {
            throw new BadRequestException("Customer Code '" + customerCode + "' is already taken");
        }

        // TODO: check already MI Code
        Boolean checkExistByCode = billingMIService.checkExistByCode(miCode);
        if (!checkExistByCode) {
            throw new BadRequestException("MI Code '" + miCode + "' not found");
        }

        // TODO: get data MI by MI Code
        BillingMIDTO billingMIDTO = billingMIService.getByCode(miCode);

        BillingCustomer billingCustomer = BillingCustomer.builder()
                    .customerCode(customerCode)
                    .customerName(request.getCustomerName())
                    .customerMinimumFee(customerMinimumFee)
                    .customerSafekeepingFee(customerSafekeepingFee)
                    .billingCategory(request.getBillingCategory())
                    .billingType(request.getBillingType())
                    .billingTemplate(request.getBillingTemplate())
                    .miCode(billingMIDTO.getCode())
                    .miName(billingMIDTO.getName())
                    .debitTransfer(request.getDebitTransfer())
                    .accountName(request.getAccountName())
                    .account(request.getAccount())
                    .costCenter(request.getCostCenter())
                    .glAccountHasil(request.getGlAccountHasil())
                    .npwpNumber(request.getNpwpNumber())
                    .npwpName(request.getNpwpName())
                    .npwpAddress(request.getNpwpAddress())
                    .kseiSafeCode(request.getKseiSafeCode())
                    .currency(request.getCurrency())
                    .sellingAgent(request.getSellingAgent())
                    .build();

        BillingCustomer billingCustomerSaved = billingCustomerRepository.save(billingCustomer);

        return mapToDTO(billingCustomerSaved);
    }

    @Override
    public UploadBillingCustomerListResponse uploadBillingCustomerList(UploadBillingCustomerRequest request) {
        String inputerId = request.getInputerId();
        String inputerIPAddress = request.getInputerIPAddress();

        List<BillingCustomerDataDTO> billingCustomerDataDTOList = new ArrayList<>();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;

        try {
            for (UploadBillingCustomerListRequest dataRequest : request.getDataListRequest()) {
                String customerCode = dataRequest.getCustomerCode();
                List<String> messageValidationList = checkValidationDataUploadRequest(dataRequest);
                List<String> errorMessages = new ArrayList<>(messageValidationList);

                Boolean isExistsBillingCustomer = billingCustomerRepository.existsByCustomerCode(customerCode);
                if (isExistsBillingCustomer) {
                    errorMessages.add("Customer Code '" + customerCode + "' is already taken");
                }

                log.info("Error messages size: {}", errorMessages.size());

                if (errorMessages.isEmpty()) {
                    String jsonAfter = objectMapper.writeValueAsString(request);
                    BillingDataChange billingDataChange = BillingDataChange.builder()
                            .approvalStatus(ApprovalStatus.Pending)
                            .inputDate(new Date())
                            .inputerId(inputerId)
                            .inputerIPAddress(inputerIPAddress)
                            .entityId(dataRequest.getCustomerCode())
                            .action(ChangeAction.Add)
                            .entityClassName(BillingCustomer.class.getName())
                            .tableName("billing_customer")
                            .jsonDataBefore("")
                            .jsonDataAfter(jsonAfter)
                            .description("")
                            .build();
                    billingDataChangeRepository.save(billingDataChange);
                    totalDataSuccess++;
                } else {
                    BillingCustomerDataDTO billingCustomerDataDTO = BillingCustomerDataDTO.builder()
                            .customerCode(customerCode)
                            .messages(errorMessages)
                            .build();
                    billingCustomerDataDTOList.add(billingCustomerDataDTO);
                    totalDataFailed++;
                }
            }

            log.info("Total data success: {}, total data failed: {}", totalDataSuccess, totalDataFailed);
            return UploadBillingCustomerListResponse.builder()
                    .totalDataSuccess(totalDataSuccess)
                    .totalDataFailed(totalDataFailed)
                    .data(billingCustomerDataDTOList)
                    .build();
        } catch (Exception e) {
            log.error("Error when upload billing customer: " + e);
            throw new BadRequestException("Bad Request: " + e.getMessage());
        }
    }

    @Override
    public UploadBillingCustomerListResponse uploadBillingCustomerListApprove(UploadBillingCustomerRequest uploadBillingCustomerRequest) {
        // Data Approval
        String dataChangeId = uploadBillingCustomerRequest.getDataChangeId();
        String approveIPAddress = uploadBillingCustomerRequest.getApproverIPAddress();

        List<BillingCustomerDataDTO> billingCustomerDataDTOList = new ArrayList<>();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;

        // TODO: validation data request
        for (UploadBillingCustomerListRequest request : uploadBillingCustomerRequest.getDataListRequest()) {
            String customerCode = request.getCustomerCode();
            String customerName = request.getCustomerName();
            BigDecimal customerMinimumFee = request.getCustomerMinimumFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerMinimumFee());
            BigDecimal customerSafekeepingFee = request.getCustomerSafekeepingFee().isEmpty() ? BigDecimal.ZERO : new BigDecimal(request.getCustomerSafekeepingFee());
            String miCode = request.getMiCode();
            log.info("Customer Code: {}, Customer Name: {}, MI Code: {}", customerCode, customerName, miCode);

            List<String> errorValidationList = checkValidationDataUploadRequest(request);
            List<String> errorMessages = new ArrayList<>(errorValidationList);
            log.info("Error messages size: {}", errorMessages.size());

            Boolean existsBillingCustomer = billingCustomerRepository.existsByCustomerCode(customerCode);
            if (existsBillingCustomer) {
                errorMessages.add("Customer Code '" + customerCode + "' is already taken");
            }

            Boolean existsMI = billingMIService.checkExistByCode(miCode);
            if (!existsMI) {
                errorMessages.add("MI Code '" + miCode + "' not found");
            }

            // TODO: get data MI by MI Code
            BillingMIDTO billingMIDTO = billingMIService.getByCode(miCode);

            log.info("Error Messages size: {}", errorMessages.size());

            if (errorMessages.isEmpty()) {
                BillingCustomer billingCustomer = BillingCustomer.builder()
                        .customerCode(customerCode)
                        .customerName(customerName)
                        .customerMinimumFee(customerMinimumFee)
                        .customerSafekeepingFee(customerSafekeepingFee)
                        .billingCategory(request.getBillingCategory())
                        .billingType(request.getBillingType())
                        .billingTemplate(request.getBillingTemplate())
                        .miCode(billingMIDTO.getCode())
                        .miName(billingMIDTO.getName())
                        .debitTransfer(request.getDebitTransfer())
                        .accountName(request.getAccountName())
                        .account(request.getAccount())
                        .costCenter(request.getCostCenter())
                        .glAccountHasil(request.getGlAccountHasil())
                        .npwpNumber(request.getNpwpNumber())
                        .npwpName(request.getNpwpName())
                        .npwpAddress(request.getNpwpAddress())
                        .kseiSafeCode(request.getKseiSafeCode())
                        .currency(request.getCurrency())
                        .sellingAgent(request.getSellingAgent())
                        .build();
                billingCustomerRepository.save(billingCustomer);
                totalDataSuccess++;

                // update data change by idDataChange as success and update status approval status is Approved
                // TODO: get object data change by id, then update data change
                BillingDataChange billingDataChange = billingDataChangeRepository.findById(Long.valueOf(dataChangeId))
                        .orElseThrow(() -> new DataNotFoundException("Billing Data Change with id '" + dataChangeId + "' not found"));
                billingDataChange.setApproverIPAddress(approveIPAddress);
                billingDataChange.setApprovalStatus(ApprovalStatus.Approved);
                billingDataChangeRepository.save(billingDataChange);
            } else {
                String separator = ", ";
                String errorMessagesJoin = String.join(separator, errorMessages);
                BillingDataChange billingDataChange = billingDataChangeRepository.findById(Long.valueOf(dataChangeId))
                        .orElseThrow(() -> new DataNotFoundException("Billing Data Change with id '" + dataChangeId + "' not found"));
                billingDataChange.setApprovalStatus(ApprovalStatus.Rejected);
                billingDataChange.setApproverIPAddress(approveIPAddress);
                billingDataChange.setDescription(errorMessagesJoin);
                billingDataChangeRepository.save(billingDataChange);
                totalDataFailed++;
            }
        }

        log.info("Total data success: {}, total data failed: {}", totalDataSuccess, totalDataFailed);
        return UploadBillingCustomerListResponse.builder()
                .totalDataSuccess(totalDataSuccess)
                .totalDataFailed(totalDataFailed)
                .data(billingCustomerDataDTOList)
                .build();
    }

    @Override
    public BillingCustomerDTO updateByCustomerCode(String customerCode, CreateBillingCustomerRequest request) {
        BillingCustomer billingCustomer = billingCustomerRepository.findByCode(customerCode)
                .orElseThrow(() -> new DataNotFoundException("Billing Customer with customer code '" + customerCode + "' not found"));

        if (!request.getCustomerName().isEmpty()) {
            billingCustomer.setCustomerName(request.getCustomerName());
        }

        if (request.getCustomerMinimumFee() == null) {
            billingCustomer.setCustomerMinimumFee(BigDecimal.ZERO);
        }

        if (request.getCustomerSafekeepingFee() == null) {
            billingCustomer.setCustomerSafekeepingFee(BigDecimal.ZERO);
        }

        billingCustomerRepository.save(billingCustomer);
        return mapToDTO(billingCustomer);
    }

    @Override
    public BillingCustomerDTO getByCustomerCode(String customerCode) {
        BillingCustomer billingCustomer = billingCustomerRepository.findByCode(customerCode)
                .orElseThrow(() -> new DataNotFoundException("Billing Customer with customer code '" + customerCode + "' not found"));
        return mapToDTO(billingCustomer);
    }

    @Override
    public BillingCustomerDTO updateById(String id, CreateBillingCustomerRequest request) {
        BillingCustomer billingCustomer = billingCustomerRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        if (billingCustomer.getAccountName() != null) {
            billingCustomer.setAccountName(request.getAccountName());
        }

        if (billingCustomer.getDebitTransfer() != null){
            billingCustomer.setDebitTransfer(request.getDebitTransfer());
        }

        if (billingCustomer.getGlAccountHasil() != null){
            billingCustomer.setGlAccountHasil(request.getGlAccountHasil());
        }


        if (billingCustomer.getNpwpNumber() != null){
            billingCustomer.setNpwpNumber(request.getNpwpNumber());
        }

        return mapToDTO(billingCustomerRepository.save(billingCustomer));
    }

    @Override
    public UpdateBillingCustomerListResponse updateBillingCustomerList(UpdateBillingCustomerRequest request) {
        List<BillingCustomerDataDTO> billingCustomerDataDTOList = new ArrayList<>();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;

        try {
            for (UpdateBillingCustomerListRequest dataRequest : request.getDataListRequest()) {
                String customerCode = dataRequest.getCustomerCode();
                String miCode = dataRequest.getMiCode();

                List<String> errorMessages = new ArrayList<>();

                // TODO: check customer code apakah ada atau tidak
                Boolean existBillingCustomer = billingCustomerRepository.existsByCustomerCode(customerCode);
                if (existBillingCustomer) {
                    errorMessages.add("Customer Code with code '" + customerCode + "' is already taken");
                }

                // TODO: check MI code apakah ada atau tidak
                Boolean existMI = billingMIService.checkExistByCode(miCode);
                if (!existMI) {
                    errorMessages.add("MI with code '" + miCode + "' not found");
                }

                if (errorMessages.size() == 0) {
                    // TODO: get Billing Customer by customer code
                    BillingCustomer billingCustomer = billingCustomerRepository.findByCode(customerCode).orElseThrow(() -> new DataNotFoundException("Billing Customer with customer code '" + customerCode + "' not found"));
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonDataBefore = objectMapper.writeValueAsString(billingCustomer);
                    String jsonDataAfter = objectMapper.writeValueAsString(dataRequest);

                    // TODO: create Billing Data Change dengan Action Edit dan Approval Status is Pending
                    BillingDataChange billingDataChange = BillingDataChange.builder()
                            .action(ChangeAction.Edit)
                            .approvalStatus(ApprovalStatus.Pending)
                            .inputDate(new Date())
                            .jsonDataBefore(jsonDataBefore)
                            .jsonDataAfter(jsonDataAfter)
                            .build();

                    billingDataChangeRepository.save(billingDataChange);
                    totalDataSuccess++;
                } else {
                    BillingCustomerDataDTO billingCustomerDataDTO = BillingCustomerDataDTO.builder()
                            .customerCode(customerCode)
                            .messages(errorMessages)
                            .build();
                    billingCustomerDataDTOList.add(billingCustomerDataDTO);
                    totalDataFailed++;
                }
            }

            log.info("Total data success: {}, total data failed: {}", totalDataSuccess, totalDataFailed);
            return UpdateBillingCustomerListResponse.builder()
                    .totalDataSuccess(totalDataSuccess)
                    .totalDataFailed(totalDataFailed)
                    .data(billingCustomerDataDTOList)
                    .build();
        } catch (Exception e) {
            log.error("Error when update billing customer");
            throw new BadRequestException("Bad Request: " + e.getMessage());
        }
    }

    @Override
    public UpdateBillingCustomerListResponse updateBillingCustomerListApprove(UpdateBillingCustomerRequest request) {

        // TODO: masukkan data Billing Customer yg mau di update ke table entity Billing Customer
        return null;
    }

    @Override
    public BillingCustomerDTO getById(String id) {
        BillingCustomer billingCustomer = billingCustomerRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Billing Customer with id '" + id + "' not found"));
        return mapToDTO(billingCustomer);
    }

    @Override
    public List<BillingCustomerDTO> getAll() {
        List<BillingCustomer> billingCustomers = billingCustomerRepository.findAll();
        return mapToDTOList(billingCustomers);
    }

    @Override
    public List<BillingCustomerDTO> getAllByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        log.info("Start get all Billing Customer by billing category '{}' and billing type '{}'", billingCategory, billingType);
        List<BillingCustomer> customerList = billingCustomerRepository.findAllByBillingCategoryAndBillingType(billingCategory, billingType);
        return mapToDTOList(customerList);
    }

    @Override
    public List<BillingCustomerDTO> getAllByBillingCategoryAndBillingTypeAndCurrency(String billingCategory, String billingType, String currency) {
        log.info("Start get all Billing Customer by billing category '{}', billing type '{}' and currency '{}'", billingCategory, billingType, currency);
        List<BillingCustomer> billingCustomerList = billingCustomerRepository.findAllByBillingCategoryAndBillingTypeAndCurrency(billingCategory, billingType, currency);
        return mapToDTOList(billingCustomerList);
    }

    @Override
    public String deleteById(String id) {
        BillingCustomer billingCustomer = billingCustomerRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Billing Customer with id '" + id + "' is not found"));
        billingCustomerRepository.delete(billingCustomer);
        return "Successfully delete billing customer by id: " + id;
    }

    private static BillingCustomerDTO mapToDTO(BillingCustomer customer) {
        return BillingCustomerDTO.builder()
                .id(String.valueOf(customer.getId()))
                .customerCode(customer.getCustomerCode())
                .customerName(customer.getCustomerName())
                .customerMinimumFee(customer.getCustomerMinimumFee())
                .customerSafekeepingFee(customer.getCustomerSafekeepingFee())
                .billingCategory(customer.getBillingCategory())
                .billingType(customer.getBillingType())
                .billingTemplate(customer.getBillingTemplate())
                .miCode(customer.getMiCode())
                .miName(customer.getMiName())
                .debitTransfer(customer.getDebitTransfer())
                .accountName(customer.getAccountName())
                .account(customer.getAccount())
                .costCenter(customer.getCostCenter())
                .glAccountHasil(customer.getGlAccountHasil())
                .npwpNumber(customer.getNpwpNumber())
                .npwpName(customer.getNpwpName())
                .npwpAddress(customer.getNpwpAddress())
                .kseiSafeCode(customer.getKseiSafeCode())
                .currency(customer.getCurrency())
                .sellingAgent(customer.getSellingAgent())
                .build(); }

    private static List<BillingCustomerDTO> mapToDTOList(List<BillingCustomer> customerList) {
        return customerList.stream()
                .map(BillingCustomerServiceImpl::mapToDTO)
                .collect(Collectors.toList());
    }

    private static BillingCustomer mapToModel(CreateBillingCustomerRequest request) {
        return BillingCustomer.builder()
                .customerCode(request.getCustomerCode())
                .customerName(request.getCustomerName())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .miCode(request.getMiCode())
                .debitTransfer(request.getDebitTransfer())
                .account(request.getAccount())
                .accountName(request.getAccountName())
                .costCenter(request.getCostCenter())
                .glAccountHasil(request.getGlAccountHasil())
                .npwpNumber(request.getNpwpNumber())
                .npwpName(request.getNpwpName())
                .npwpAddress(request.getNpwpAddress())
                .kseiSafeCode(request.getKseiSafeCode())
                .currency(request.getCurrency())
                .sellingAgent(request.getSellingAgent())
                .build();
    }

    private static BillingCustomer mapToModelUpload(UploadBillingCustomerListRequest request) {
        return BillingCustomer.builder()
                .customerCode(request.getCustomerCode())
                .customerName(request.getCustomerName())
                .billingCategory(request.getBillingCategory())
                .billingType(request.getBillingType())
                .miCode(request.getMiCode())
                .debitTransfer(request.getDebitTransfer())
                .account(request.getAccount())
                .accountName(request.getAccountName())
                .costCenter(request.getCostCenter())
                .glAccountHasil(request.getGlAccountHasil())
                .npwpNumber(request.getNpwpNumber())
                .npwpName(request.getNpwpName())
                .npwpAddress(request.getNpwpAddress())
                .kseiSafeCode(request.getKseiSafeCode())
                .currency(request.getCurrency())
                .sellingAgent(request.getSellingAgent())
                .build();
    }

    private static List<BillingCustomer> mapToModelList(List<CreateBillingCustomerRequest> requests) {
        return requests.stream()
                .map(BillingCustomerServiceImpl::mapToModel)
                .collect(Collectors.toList());
    }

    private static List<BillingCustomer> mapToModelListUpload(List<UploadBillingCustomerListRequest> requests) {
        return requests.stream()
                .map(BillingCustomerServiceImpl::mapToModelUpload)
                .collect(Collectors.toList());
    }


    private static List<String> checkValidationDataUploadRequest(UploadBillingCustomerListRequest request) {
        // harus checking 1 per 1

        List<String> stringList = new ArrayList<>();

        if (request.getCustomerName().isEmpty()) {
            stringList.add("Customer Name must not be blank");
        }

        if (request.getCustomerCode().isEmpty()) {
            stringList.add("Customer Code must not be blank");
        }

        if (request.getMiCode().isEmpty()) {
            stringList.add("MI Code must not be blank");
        }

        // String patternStr =  "^([^.@]+)(\.[^.@]+)*@([^.@]+\.)+([^.@]+)$";

//        private static final String EMAIL_PATTERN = "^([^.@]+)(\.[^.@]+)*@([^.@]+\.)+([^.@]+)$";
//
//        public boolean isValidEmail(String email) {
//            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
//            return pattern.matcher(email).matches();
//        }

        return stringList;
    }

}
