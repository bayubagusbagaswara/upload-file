package com.services.billingservice.service.placement.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.billingservice.dto.ErrorMessageDTO;
import com.services.billingservice.dto.placement.datachange.PlacementDataChangeDTO;
import com.services.billingservice.dto.placement.instructionsinvest.*;
import com.services.billingservice.dto.placement.result.ResultGeneric;
import com.services.billingservice.dto.placement.validation.AddValidationGroup;
import com.services.billingservice.dto.placement.validation.UpdateValidationGroup;
import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.exception.placement.TooManyRequestException;
import com.services.billingservice.mapper.placement.InstructionsSInvestMapper;
import com.services.billingservice.mapper.placement.PlacementDataChangeMapper;
import com.services.billingservice.model.placement.PlacementDataChange;
import com.services.billingservice.model.placement.InstructionsSInvest;
import com.services.billingservice.repository.placement.InstructionsSInvestRepository;
import com.services.billingservice.service.placement.InstructionsSInvestService;
import com.services.billingservice.service.placement.PlacementDataChangeService;
import com.services.billingservice.utils.placement.PlacementJsonUtil;
import com.services.billingservice.utils.placement.ValidationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import static com.services.billingservice.constant.placement.PlacementConstant.*;
import static com.services.billingservice.utils.placement.RequestDataUtil.setIfNullOrEmpty;
import static com.services.billingservice.utils.placement.RequestDataUtil.trimIfNotNull;

@Service
@Slf4j
public class InstructionsSInvestServiceImpl implements InstructionsSInvestService {

    private final InstructionsSInvestRepository instructionsSInvestRepository;
    private final ObjectMapper objectMapper;
    private final ValidationData validationData;
    private final PlacementDataChangeService placementDataChangeService;
    private final PlacementDataChangeMapper placementDataChangeMapper;
    private final InstructionsSInvestMapper instructionSInvestMapper;
    private final Semaphore semaphore = new Semaphore(1, true);
    private final ExecutorService executor;

    public InstructionsSInvestServiceImpl(InstructionsSInvestRepository instructionsSInvestRepository, ObjectMapper objectMapper, ValidationData validationData, PlacementDataChangeService placementDataChangeService,
                                          PlacementDataChangeMapper placementDataChangeMapper, InstructionsSInvestMapper instructionSInvestMapper,
                                          @Qualifier("singleThreadExecutor") ExecutorService executor) {
        this.instructionsSInvestRepository = instructionsSInvestRepository;
        this.objectMapper = objectMapper;
        this.validationData = validationData;
        this.placementDataChangeService = placementDataChangeService;
        this.placementDataChangeMapper = placementDataChangeMapper;
        this.instructionSInvestMapper = instructionSInvestMapper;
        this.executor = executor;
    }

    @Override
    public boolean isSiReferenceIDAlreadyExists(String siReferenceId) {
        return instructionsSInvestRepository.existsBySiReferenceId(siReferenceId);
    }

    @Override
    public CompletableFuture<InstructionsSInvestResponse> uploadData(UploadInstructionsSInvestListRequest uploadInstructionsSInvestListRequest, PlacementDataChangeDTO placementDataChangeDTO) {
        log.info("Start upload data Instructions S Invest: {}, {}", uploadInstructionsSInvestListRequest, placementDataChangeDTO);

        if (!semaphore.tryAcquire()) {
            CompletableFuture<InstructionsSInvestResponse> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new TooManyRequestException(1, 1));
            return failed;
        }

        return CompletableFuture.supplyAsync(() -> {
            int totalDataSuccess = 0;
            int totalDataFailed = 0;
            List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

            try {
                for (UploadInstructionsSInvestDataRequest uploadInstructionsSInvestDataRequest : uploadInstructionsSInvestListRequest.getUploadInstructionsSInvestDataRequestList()) {
                    List<String> validationErrors = new ArrayList<>();
                    InstructionsSInvestDTO instructionsSInvestDTO = null;

                    try {
                        LocalDate requestDate = LocalDate.parse(uploadInstructionsSInvestDataRequest.getPlacementDate());
                        if (!requestDate.isEqual(LocalDate.now())) {
                            totalDataFailed++;
                            errorMessageDTOList.add(new ErrorMessageDTO(uploadInstructionsSInvestDataRequest.getSiReferenceId(), Collections.singletonList("Placement Date does not match current date")));
                            continue;
                        }

                        ResultGeneric<InstructionsSInvest> violationResult = getViolationResult(uploadInstructionsSInvestDataRequest);

                        if (violationResult.hasViolations()) {
                            createErrorViolation(uploadInstructionsSInvestDataRequest, violationResult, validationErrors, errorMessageDTOList);
                            totalDataFailed++;
                        } else {
                            instructionsSInvestDTO = instructionSInvestMapper.fromUploadRequestToDTO(uploadInstructionsSInvestDataRequest);

                            if (violationResult.isExistingDataPresent()) {
                                log.info("Duplicate SI Reference ID: {}", instructionsSInvestDTO);
                                errorMessageDTOList.add(new ErrorMessageDTO(instructionsSInvestDTO.getSiReferenceId(), Collections.singletonList("SI Reference is already exists")));
                                totalDataFailed++;
                            } else {
                                log.info("Add: {}", instructionsSInvestDTO);
                                handleNewInstructionsSInvest(instructionsSInvestDTO, placementDataChangeDTO);
                                totalDataSuccess++;
                            }
                        }
                    } catch (Exception e) {
                        handleGeneralError(instructionsSInvestDTO, e, validationErrors, errorMessageDTOList);
                        totalDataFailed++;
                    }
                }
                return new InstructionsSInvestResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
            } finally {
                semaphore.release();
            }
        }, executor);
    }

    @Transactional
    @Override
    public InstructionsSInvestResponse createApprove(ApproveInstructionsSInvestRequest approveInstructionsSInvestRequest, String approveIPAddress) {
        log.info("Start create approve Instructions S-Invest: {}, {}", approveInstructionsSInvestRequest, approveIPAddress);
        String approveId = approveInstructionsSInvestRequest.getApproverId();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        InstructionsSInvestDTO instructionsSInvestDTO = null;

        try {
            PlacementDataChange dataChange = placementDataChangeService.getById(approveInstructionsSInvestRequest.getDataChangeId());

            instructionsSInvestDTO = objectMapper.readValue(dataChange.getJsonDataAfter(), InstructionsSInvestDTO.class);

            /* change to SI Reference Id, because it is unique data */
            validationSiReferenceIdAlreadyExists(instructionsSInvestDTO.getSiReferenceId(), validationErrors);

            if (!validationErrors.isEmpty()) {
                placementDataChangeService.setApprovalStatusIsRejected(dataChange, validationErrors);
                totalDataFailed++;
            } else {
                LocalDateTime approveDate = LocalDateTime.now();

                InstructionsSInvest instructionsSInvest = InstructionsSInvest.builder()
                        .approvalStatus(ApprovalStatus.Approved)
                        .approverId(approveId)
                        .approveDate(approveDate)
                        .approveIPAddress(approveIPAddress)
                        .inputerId(dataChange.getInputerId())
                        .inputDate(dataChange.getInputDate())
                        .inputIPAddress(dataChange.getInputIPAddress())
                        .imCode(instructionsSInvestDTO.getImCode())
                        .imName(instructionsSInvestDTO.getImName())
                        .fundCode(instructionsSInvestDTO.getFundCode())
                        .fundName(instructionsSInvestDTO.getFundName())
                        .placementBankCode(instructionsSInvestDTO.getPlacementBankCode())
                        .placementBankName(instructionsSInvestDTO.getPlacementBankName())
                        .placementBankCashAccountName(instructionsSInvestDTO.getPlacementBankCashAccountName())
                        .placementBankCashAccountNo(instructionsSInvestDTO.getPlacementBankCashAccountNo())
                        .currency(instructionsSInvestDTO.getCurrency())
                        .principle(parseStringToBigDecimal(instructionsSInvestDTO.getPrinciple()))
                        .placementDate(parseStringToLocalDate(instructionsSInvestDTO.getPlacementDate()))
                        .referenceNo(instructionsSInvestDTO.getReferenceNo())
                        .siReferenceId(instructionsSInvestDTO.getSiReferenceId())
                        .build();

                InstructionsSInvest save = instructionsSInvestRepository.save(instructionsSInvest);

                dataChange.setApproverId(approveId);
                dataChange.setApproveDate(approveDate);
                dataChange.setApproveIPAddress(approveIPAddress);
                dataChange.setEntityId(save.getId().toString());
                dataChange.setJsonDataAfter(
                        PlacementJsonUtil.cleanedEntityDataFromApprovalData(
                                objectMapper.writeValueAsString(save)
                        )
                );
                dataChange.setDescription("Success create approve with id: " + save.getId());
                placementDataChangeService.setApprovalStatusIsApproved(dataChange);

                totalDataSuccess++;
            }
        } catch (Exception e) {
            handleGeneralError(instructionsSInvestDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new InstructionsSInvestResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Transactional
    @Override
    public InstructionsSInvestResponse updateApprove(ApproveInstructionsSInvestRequest approveInstructionsSInvestRequest, String approveIPAddress) {
        log.info("Start update approve Instruction S-Invest: {}, {}", approveInstructionsSInvestRequest, approveIPAddress);
        String approveId = approveInstructionsSInvestRequest.getApproverId();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        InstructionsSInvestDTO instructionsSInvestDTO = null;

        try {
            PlacementDataChange placementDataChange = placementDataChangeService.getById(approveInstructionsSInvestRequest.getDataChangeId());

            instructionsSInvestDTO = objectMapper.readValue(placementDataChange.getJsonDataAfter(), InstructionsSInvestDTO.class);

            InstructionsSInvest instructionsSInvest = instructionsSInvestRepository.findById(Long.valueOf(placementDataChange.getEntityId()))
                    .orElseThrow(() -> new DataNotFoundException(S_INVEST_ID_NOT_FOUND + placementDataChange.getEntityId()));

            if (!instructionsSInvestDTO.getImCode().isEmpty()) {
                instructionsSInvest.setImCode(instructionsSInvestDTO.getImCode());
            }

            if (!instructionsSInvestDTO.getImName().isEmpty()) {
                instructionsSInvest.setImName(instructionsSInvestDTO.getImName());
            }

            if (!instructionsSInvestDTO.getFundCode().isEmpty()) {
                instructionsSInvest.setFundCode(instructionsSInvestDTO.getFundCode());
            }

            if (!instructionsSInvestDTO.getFundName().isEmpty()) {
                instructionsSInvest.setFundName(instructionsSInvestDTO.getFundName());
            }

            if (!instructionsSInvestDTO.getPlacementBankCode().isEmpty()) {
                instructionsSInvest.setPlacementBankCode(instructionsSInvestDTO.getPlacementBankCode());
            }

            // placementBankName
            if (!instructionsSInvestDTO.getPlacementBankName().isEmpty()) {
                instructionsSInvest.setPlacementBankName(instructionsSInvestDTO.getPlacementBankName());
            }

            // placementBankCashAccountName
            if (!instructionsSInvestDTO.getPlacementBankCashAccountName().isEmpty()) {
                instructionsSInvest.setPlacementBankCashAccountName(instructionsSInvestDTO.getPlacementBankCashAccountName());
            }

            // placementBankCashAccountNo
            if (!instructionsSInvestDTO.getPlacementBankCashAccountNo().isEmpty()) {
                instructionsSInvest.setPlacementBankCashAccountNo(instructionsSInvestDTO.getPlacementBankCashAccountNo());
            }

            // currency
            if (!instructionsSInvestDTO.getCurrency().isEmpty()) {
                instructionsSInvest.setCurrency(instructionsSInvestDTO.getCurrency());
            }

            // principle
            if (!instructionsSInvestDTO.getPrinciple().isEmpty()) {
                instructionsSInvest.setPrinciple(parseStringToBigDecimal(instructionsSInvestDTO.getPrinciple()));
            }

            // placementDate
            if (!instructionsSInvestDTO.getPlacementDate().isEmpty()) {
                instructionsSInvest.setPlacementDate(parseStringToLocalDate(instructionsSInvestDTO.getPlacementDate()));
            }

            // referenceNo
            if (!instructionsSInvestDTO.getReferenceNo().isEmpty()) {
                instructionsSInvest.setReferenceNo(instructionsSInvestDTO.getReferenceNo());
            }

            LocalDateTime approveDate = LocalDateTime.now();

            instructionsSInvest.setApprovalStatus(ApprovalStatus.Approved);
            instructionsSInvest.setApproverId(approveId);
            instructionsSInvest.setApproveIPAddress(approveIPAddress);
            instructionsSInvest.setApproveDate(approveDate);
            instructionsSInvest.setInputerId(placementDataChange.getInputerId());
            instructionsSInvest.setInputIPAddress(placementDataChange.getInputIPAddress());
            instructionsSInvest.setInputDate(placementDataChange.getInputDate());

            InstructionsSInvest save = instructionsSInvestRepository.save(instructionsSInvest);

            placementDataChange.setApproverId(approveId);
            placementDataChange.setApproveDate(approveDate);
            placementDataChange.setApproveIPAddress(approveIPAddress);
            placementDataChange.setJsonDataAfter(
                    PlacementJsonUtil.cleanedEntityDataFromApprovalData(
                            objectMapper.writeValueAsString(save)
                    )
            );
            placementDataChange.setDescription("Success update approve with id: " + save.getId());
            placementDataChangeService.setApprovalStatusIsApproved(placementDataChange);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(instructionsSInvestDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new InstructionsSInvestResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Transactional
    @Override
    public InstructionsSInvestResponse deleteById(DeleteInstructionsSInvestRequest deleteInstructionsSInvestRequest, PlacementDataChangeDTO placementDataChangeDTO) {
        log.info("Start delete Instruction S-Invest by id: {}, {}", deleteInstructionsSInvestRequest, placementDataChangeDTO);
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        InstructionsSInvestDTO instructionsSInvestDTO = null;

        try {
            InstructionsSInvest instructionsSInvest = instructionsSInvestRepository.findById(deleteInstructionsSInvestRequest.getId())
                    .orElseThrow(() -> new DataNotFoundException(S_INVEST_ID_NOT_FOUND + deleteInstructionsSInvestRequest.getId()));

            instructionsSInvestDTO = instructionSInvestMapper.toDTO(instructionsSInvest);

            placementDataChangeDTO.setEntityId(instructionsSInvest.getId().toString());
            placementDataChangeDTO.setJsonDataBefore(
                    PlacementJsonUtil.cleanedEntityDataFromApprovalData(
                            objectMapper.writeValueAsString(instructionsSInvest)
                    )
            );
            placementDataChangeDTO.setJsonDataAfter("");
            PlacementDataChange placementDataChange = placementDataChangeMapper.toModel(placementDataChangeDTO);
            placementDataChangeService.createChangeActionDelete(placementDataChange, InstructionsSInvest.class);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(instructionsSInvestDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new InstructionsSInvestResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Transactional
    @Override
    public InstructionsSInvestResponse deleteApprove(ApproveInstructionsSInvestRequest approveInstructionsSInvestRequest, String approveIPAddress) {
        log.info("Delete approve Instruction S-Invest: {}, {}", approveInstructionsSInvestRequest, approveIPAddress);
        String approveId = approveInstructionsSInvestRequest.getApproverId();
        int totalDataSuccess = 0;
        int totalDataFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        InstructionsSInvestDTO instructionsSInvestDTO = null;

        try {
            PlacementDataChange placementDataChange = placementDataChangeService.getById(approveInstructionsSInvestRequest.getDataChangeId());

            InstructionsSInvest instructionsSInvest = instructionsSInvestRepository.findById(Long.valueOf(placementDataChange.getEntityId()))
                    .orElseThrow(() -> new DataNotFoundException(S_INVEST_ID_NOT_FOUND + placementDataChange.getEntityId()));

            instructionsSInvestDTO = instructionSInvestMapper.toDTO(instructionsSInvest);

            placementDataChange.setApproverId(approveId);
            placementDataChange.setApproveDate(LocalDateTime.now());
            placementDataChange.setApproveIPAddress(approveIPAddress);
            placementDataChange.setDescription("Success delete approve with id: " + instructionsSInvest.getId());

            placementDataChangeService.setApprovalStatusIsApproved(placementDataChange);

            /* delete entity */
            instructionsSInvestRepository.delete(instructionsSInvest);
            totalDataSuccess++;
        } catch (Exception e) {
            handleGeneralError(instructionsSInvestDTO, e, validationErrors, errorMessageDTOList);
            totalDataFailed++;
        }
        return new InstructionsSInvestResponse(totalDataSuccess, totalDataFailed, errorMessageDTOList);
    }

    @Override
    public InstructionsSInvestDTO getById(Long id) {
        log.info("Start get Instructions S-Invest get by id: {}", id);
        InstructionsSInvest instructionsSInvest = instructionsSInvestRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(S_INVEST_ID_NOT_FOUND + id));
        return instructionSInvestMapper.toDTO(instructionsSInvest);
    }

    @Override
    public List<InstructionsSInvestDTO> getAll() {
        log.info("Start get all Instructions S-Invest");
        List<InstructionsSInvest> all = instructionsSInvestRepository.findAll();
        return instructionSInvestMapper.toDTOList(all);
    }

    @Override
    public List<InstructionsSInvest> getAllByPlacementDate(LocalDate today) {
        log.info("Start get all today Instructions S-Invest: {}", today);
        return instructionsSInvestRepository.findByPlacementDate(today);
    }

    @Override
    public List<String> getDuplicateDataSiReferenceId(LocalDate today) {
        log.info("Start get Duplicate Data SiReferenceId of today Instructions S-Invest: {}", today);
        return instructionsSInvestRepository.findDuplicateDataPlacementReferenceIds(today);
    }

    private void handleGeneralError(InstructionsSInvestDTO dto, Exception e, List<String> validationErrors, List<ErrorMessageDTO> errorMessageDTOList) {
        log.error("An unexpected error occurred: {}", e.getMessage(), e);
        validationErrors.add(e.getMessage());
        errorMessageDTOList.add(
                new ErrorMessageDTO(
                        dto != null && !dto.getSiReferenceId().isEmpty() ? dto.getSiReferenceId() : S_INVEST_UNKNOWN_SI_REFERENCE_ID,
                        validationErrors
                )
        );
    }

    private void validationSiReferenceIdAlreadyExists(String siReferenceId, List<String> validationErrors) {
        if (isSiReferenceIDAlreadyExists(siReferenceId)) {
            validationErrors.add("S-Invest is already taken with SI Reference ID: " + siReferenceId);
        }
    }

    private void handleNewInstructionsSInvest(InstructionsSInvestDTO instructionsSInvestDTO, PlacementDataChangeDTO placementDataChangeDTO) throws JsonProcessingException {
        placementDataChangeDTO.setMethodHttp(HttpMethod.POST.name());
        placementDataChangeDTO.setEndpoint(S_INVEST_CREATE_APPROVE_URL);

        placementDataChangeDTO.setJsonDataAfter(
                PlacementJsonUtil.cleanedId(
                        objectMapper.writeValueAsString(instructionsSInvestDTO)
                )
        );
        PlacementDataChange placementDataChange = placementDataChangeMapper.toModel(placementDataChangeDTO);
        placementDataChangeService.createChangeActionAdd(placementDataChange, InstructionsSInvest.class);
    }

    private ResultGeneric<InstructionsSInvest> getViolationResult(UploadInstructionsSInvestDataRequest uploadInstructionsSInvestDataRequest) {
        trimRequestData(uploadInstructionsSInvestDataRequest);
        Set<ConstraintViolation<Object>> violations;
        InstructionsSInvest existingInstructionsSInvest = instructionsSInvestRepository.findBySiReferenceId(uploadInstructionsSInvestDataRequest.getSiReferenceId())
                .orElse(null);

        if (existingInstructionsSInvest != null) {
            populateOwnerGroupDataRequest(uploadInstructionsSInvestDataRequest, existingInstructionsSInvest);
            violations = validationData.validateObject(uploadInstructionsSInvestDataRequest, UpdateValidationGroup.class);
        } else {
            violations = validationData.validateObject(uploadInstructionsSInvestDataRequest, AddValidationGroup.class);
        }

        return ResultGeneric.<InstructionsSInvest>builder()
                .violations(violations)
                .data(existingInstructionsSInvest)
                .build();
    }

    private void createErrorViolation(UploadInstructionsSInvestDataRequest ownerGroupDataRequest, ResultGeneric<InstructionsSInvest> result, List<String> validationErrors, List<ErrorMessageDTO> errorMessageDTOList) {
        for (ConstraintViolation<Object> violation : result.getViolations()) {
            validationErrors.add(violation.getMessage());
        }
        errorMessageDTOList.add(new ErrorMessageDTO(ownerGroupDataRequest.getSiReferenceId(), validationErrors));
    }

    private void trimRequestData(UploadInstructionsSInvestDataRequest dataRequest) {
        trimIfNotNull(dataRequest::getImCode, dataRequest::setImCode);
        trimIfNotNull(dataRequest::getImName, dataRequest::setImName);
        trimIfNotNull(dataRequest::getFundCode, dataRequest::setFundCode);
        trimIfNotNull(dataRequest::getFundName, dataRequest::setFundName);
        trimIfNotNull(dataRequest::getPlacementBankCode, dataRequest::setPlacementBankCode);
        trimIfNotNull(dataRequest::getPlacementBankName, dataRequest::setPlacementBankName);
        trimIfNotNull(dataRequest::getPlacementBankCashAccountName, dataRequest::setPlacementBankCashAccountName);
        trimIfNotNull(dataRequest::getPlacementBankCashAccountNo, dataRequest::setPlacementBankCashAccountNo);
        trimIfNotNull(dataRequest::getCurrency, dataRequest::setCurrency);
        trimIfNotNull(dataRequest::getPrinciple, dataRequest::setPrinciple);
        trimIfNotNull(dataRequest::getPlacementDate, dataRequest::setPlacementDate);
        trimIfNotNull(dataRequest::getReferenceNo, dataRequest::setReferenceNo);
        trimIfNotNull(dataRequest::getSiReferenceId, dataRequest::setSiReferenceId);
    }

    private void populateOwnerGroupDataRequest(UploadInstructionsSInvestDataRequest dataRequest, InstructionsSInvest dataEntity) {
        setIfNullOrEmpty(dataRequest::getImCode, dataRequest::setImCode, dataEntity.getImCode());
        setIfNullOrEmpty(dataRequest::getImName, dataRequest::setImName, dataEntity.getImName());
        setIfNullOrEmpty(dataRequest::getFundCode, dataRequest::setFundCode, dataEntity.getFundCode());
        setIfNullOrEmpty(dataRequest::getFundName, dataRequest::setFundName, dataEntity.getFundName());
        setIfNullOrEmpty(dataRequest::getPlacementBankCode, dataRequest::setPlacementBankCode, dataEntity.getPlacementBankCode());
        setIfNullOrEmpty(dataRequest::getPlacementBankName, dataRequest::setPlacementBankName, dataEntity.getPlacementBankName());
        setIfNullOrEmpty(dataRequest::getPlacementBankCashAccountName, dataRequest::setPlacementBankCashAccountName, dataEntity.getPlacementBankCashAccountName());
        setIfNullOrEmpty(dataRequest::getPlacementBankCashAccountNo, dataRequest::setPlacementBankCashAccountNo, dataEntity.getPlacementBankCashAccountNo());
        setIfNullOrEmpty(dataRequest::getCurrency, dataRequest::setCurrency, dataEntity.getCurrency());
        setIfNullOrEmpty(dataRequest::getPrinciple, dataRequest::setPrinciple, dataEntity.getPrinciple().toPlainString());
        setIfNullOrEmpty(dataRequest::getPlacementDate, dataRequest::setPlacementDate, parseLocalDateToString(dataEntity.getPlacementDate().toString()));
        setIfNullOrEmpty(dataRequest::getReferenceNo, dataRequest::setReferenceNo, dataEntity.getReferenceNo());
    }

    private String parseLocalDateToString(String localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(localDate); // Parse input string to LocalDate
        return date.format(formatter); // Format LocalDate to yyyyMMdd
    }

    public static LocalDate parseStringToLocalDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: {}", dateString);
            return null; // Or throw an exception based on your requirement
        }
    }

    private BigDecimal parseStringToBigDecimal(String value) {
        String normalized;
        if (null == value || value.trim().isEmpty()) {
            normalized = "0";
        } else {
            normalized = value;
        }
        return new BigDecimal(normalized);
    }

}
