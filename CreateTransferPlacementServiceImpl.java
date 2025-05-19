package com.services.billingservice.service.placement.impl;

import com.services.billingservice.dto.ErrorMessageDTO;
import com.services.billingservice.dto.placement.createtransferplacement.*;
import com.services.billingservice.enums.placement.TransferPlacementType;
import com.services.billingservice.exception.placement.InconsistentDataException;
import com.services.billingservice.exception.placement.TooManyRequestException;
import com.services.billingservice.model.placement.PlacementApproval;
import com.services.billingservice.model.placement.PlacementData;
import com.services.billingservice.model.placement.TransactionLimit;
import com.services.billingservice.repository.placement.PlacementApprovalRepository;
import com.services.billingservice.repository.placement.PlacementDataRepository;
import com.services.billingservice.service.placement.PlacementDataService;
import com.services.billingservice.service.placement.CreateTransferPlacementService;
import com.services.billingservice.service.placement.TransactionLimitService;
import com.services.billingservice.utils.placement.GenerateUniqueKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static com.services.billingservice.constant.placement.PlacementConstant.PLACEMENT_TYPE_BULK;
import static com.services.billingservice.constant.placement.PlacementConstant.PLACEMENT_TYPE_SINGLE;
import static com.services.billingservice.enums.ApprovalStatus.Pending;

@Service
@Slf4j
public class CreateTransferPlacementServiceImpl implements CreateTransferPlacementService {

    private final PlacementApprovalRepository placementApprovalRepository;
    private final PlacementDataService placementDataService;
    private final TransactionLimitService transactionLimitService;
    private final PlacementDataRepository placementDataRepository;
    private final Semaphore semaphore = new Semaphore(1, true);
    private final ExecutorService executor;

    public CreateTransferPlacementServiceImpl(PlacementApprovalRepository placementApprovalRepository, PlacementDataService placementDataService,
                                              TransactionLimitService transactionLimitService, PlacementDataRepository placementDataRepository,
                                              @Qualifier("singleThreadExecutor") ExecutorService executor) {
        this.placementApprovalRepository = placementApprovalRepository;
        this.placementDataService = placementDataService;
        this.transactionLimitService = transactionLimitService;
        this.placementDataRepository = placementDataRepository;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<CreatePlacementResponse> createBulk(CreateBulkPlacementListRequest createBulkPlacementListRequest, String inputId, String inputIPAddress) {
        log.info("Start create Bulk transfer placement: {}, {}, {}", createBulkPlacementListRequest.getCreateBulkPlacementRequestList(), inputId, inputIPAddress);

        if (!semaphore.tryAcquire()) {
            CompletableFuture<CreatePlacementResponse> failed = new CompletableFuture<>();
            failed.completeExceptionally(
                    new TooManyRequestException(1, 1)
            );
            return failed;
        }

        return CompletableFuture.supplyAsync(() -> {
            List<CreateBulkPlacementRequest> createBulkPlacementRequestList = createBulkPlacementListRequest.getCreateBulkPlacementRequestList();
            List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
            int totalSuccess = 0;
            int totalFailed = 0;

            try {
                if (createBulkPlacementRequestList.isEmpty()) {
                    log.warn("PlacementDepositDataRequestList is empty");
                    return CreatePlacementResponse.builder()
                            .totalDataSuccess(0)
                            .totalDataFailed(0)
                            .errorMessageDTOList(Collections.singletonList(new ErrorMessageDTO("EMPTY_LIST", Collections.singletonList("Request list is empty"))))
                            .build();
                }

                List<PlacementData> placementDataList = new ArrayList<>();

                for (CreateBulkPlacementRequest request : createBulkPlacementRequestList) {
                    try {
                        PlacementData placementData = placementDataService.getById(request.getId());
                        placementDataList.add(placementData);
                    } catch (Exception e) {
                        totalFailed++;
                        errorMessageDTOList.add(new ErrorMessageDTO(request.getId().toString(), Collections.singletonList("Error fetching PlacementData: " + e.getMessage())));
                    }
                }

                validateBulkData(placementDataList);

                if (placementDataList.isEmpty()) {
                    log.warn("No valid PlacementData found for the given IDs");
                    return CreatePlacementResponse.builder()
                            .totalDataSuccess(totalSuccess)
                            .totalDataFailed(totalFailed)
                            .errorMessageDTOList(errorMessageDTOList)
                            .build();
                }

                // List of String siReferenceID
                List<String> siReferenceIDList = placementDataList.stream()
                        .map(PlacementData::getSiReferenceId)
                        .collect(Collectors.toList());
                List<String> referenceNoList = placementDataList.stream()
                        .map(PlacementData::getReferenceNo)
                        .collect(Collectors.toList());

                try {
                    PlacementData placementData = placementDataList.get(0);
                    BigDecimal totalAmount = placementDataList.stream()
                            .map(PlacementData::getPrinciple)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Transaction Type : BI-FAST, RTGS, SKN, OVERBOOKING
                    String transactionType = validateTransferTypeEnum(createBulkPlacementListRequest.getPlacementTransferType());
                    TransactionLimit transactionLimit = transactionLimitService.getByTransactionType(transactionType);
                    if (totalAmount.compareTo(transactionLimit.getMinAmount()) < 0 || totalAmount.compareTo(transactionLimit.getMaxAmount()) > 0) {
                        throw new IllegalArgumentException("Amount " + totalAmount + " is out of range for transaction type " + transactionType);
                    }

                    LocalDateTime now = LocalDateTime.now();
                    PlacementApproval placementApproval = PlacementApproval.builder()
                            .approvalStatus(Pending)
                            .inputerId(inputId)
                            .inputDate(now)
                            .inputIPAddress(inputIPAddress)
                            .approverId(null)
                            .approveDate(null)
                            .approveIPAddress(null)
                            .imCode(placementData.getImCode())
                            .imName(placementData.getImName())
                            .fundCode(placementData.getFundCode())
                            .fundName(placementData.getFundName())
                            .placementBankCode(placementData.getPlacementBankCode())
                            .placementBankName(placementData.getPlacementBankName())
                            .placementBankCashAccountName(placementData.getPlacementBankCashAccountName())
                            .placementBankCashAccountNo(placementData.getPlacementBankCashAccountNo())
                            .currency(placementData.getCurrency())
                            .principle(totalAmount)
                            .placementDate(placementData.getPlacementDate())
                            .referenceNo(String.join(", ", referenceNoList))
                            .siReferenceId(String.join(", ", siReferenceIDList))
                            .accountDebitNo(placementData.getAccountDebitNo())
                            .biCode(placementData.getBiCode())
                            .description(createBulkPlacementListRequest.getDescription())
                            .placementType(placementData.getPlacementType()) // External or Internal
                            .placementProcessType(PLACEMENT_TYPE_BULK)
                            .placementTransferType(transactionType) // RTGS, SKN, BI-FAST, OVERBOOKING
                            .referenceId(GenerateUniqueKeyUtil.generateReferenceId())
                            .inquiryReferenceId(null)
                            .ncbsStatus(null)
                            .ncbsResponseCode(null)
                            .ncbsResponseMessage(null)
                            .payUserRefNo(null)
                            .branchCode(placementData.getBranchCode())
                            .build();

//            PlacementApproval save = placementApprovalRepository.save(placementApproval);
//
//            for (CreateBulkPlacementRequest createBulkPlacementRequest : createBulkPlacementRequestList) {
//                PlacementData placementDataUpdated = placementDataService.updatePlacementApprovalIdAndStatus(
//                        createBulkPlacementRequest.getId(), save.getId(), save.getApprovalStatus().getStatus());
//                log.info("[Bulk] Placement deposit approval id: {}, placement data id: {}", placementDataUpdated.getPlacementApprovalId(), placementDataUpdated.getId());
//                totalSuccess++;
//            }

                    totalSuccess = savePlacementApprovalDataBulk(placementApproval, createBulkPlacementRequestList, totalSuccess, siReferenceIDList);
                } catch (Exception e) {
                    totalFailed++;
                    errorMessageDTOList.add(new ErrorMessageDTO("SAVE_ERROR", Collections.singletonList("Error saving PlacementApproval: " + e.getMessage())));
                }

                return CreatePlacementResponse.builder()
                        .totalDataSuccess(totalSuccess)
                        .totalDataFailed(totalFailed)
                        .errorMessageDTOList(errorMessageDTOList)
                        .build();
            } finally {
                semaphore.release();
            }
        }, executor);
    }

    @Transactional
    private Integer savePlacementApprovalDataBulk(PlacementApproval placementApproval, List<CreateBulkPlacementRequest> createBulkPlacementRequestList, Integer totalSuccess, List<String> siReferenceIDList) {
        Boolean savingCheck = placementDataRepository.findBySiReferenceIdInAndPlacementApprovalId(siReferenceIDList, "", siReferenceIDList.size());
        if (savingCheck) {
            PlacementApproval save = placementApprovalRepository.save(placementApproval);

            for (CreateBulkPlacementRequest createBulkPlacementRequest : createBulkPlacementRequestList) {
                PlacementData placementDataUpdated = placementDataService.updatePlacementApprovalIdAndStatus(
                        createBulkPlacementRequest.getId(), save.getId(), save.getApprovalStatus().getStatus());
                log.info("[Bulk] Placement deposit approval id: {}, placement data id: {}", placementDataUpdated.getPlacementApprovalId(), placementDataUpdated.getId());
                totalSuccess++;
            }
        } else {
            log.info("[Bulk] Placement deposit for siReferenceID: {} already saved by another session.", String.join(", ", siReferenceIDList));
        }

        return totalSuccess;
    }

    @Override
    public CompletableFuture<CreatePlacementResponse> createSingle(CreateSinglePlacementListRequest createSinglePlacementListRequest, String inputId, String inputIPAddress) {
        log.info("Start create Single transfer placement: {}, {}, {}", createSinglePlacementListRequest.getCreateSinglePlacementRequestList(), inputId, inputIPAddress);
        List<CreateSinglePlacementRequest> createSinglePlacementRequestList = createSinglePlacementListRequest.getCreateSinglePlacementRequestList();
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();
        int totalSuccess = 0;
        int totalFailed = 0;

        if (createSinglePlacementRequestList.isEmpty()) {
            return CreatePlacementResponse.builder()
                    .totalDataSuccess(0)
                    .totalDataFailed(0)
                    .errorMessageDTOList(Collections.singletonList(new ErrorMessageDTO("EMPTY_LIST", Collections.singletonList("Request list is empty"))))
                    .build();
        }

        for (CreateSinglePlacementRequest request : createSinglePlacementRequestList) {
            try {
                PlacementData placementData = placementDataService.getById(request.getId());
                BigDecimal amount = placementData.getPrinciple();

                String transactionType = validateTransferTypeEnum(request.getPlacementTransferType());
                TransactionLimit transactionLimit = transactionLimitService.getByTransactionType(transactionType);
                if (amount.compareTo(transactionLimit.getMinAmount()) < 0 || amount.compareTo(transactionLimit.getMaxAmount()) > 0) {
                    throw new IllegalArgumentException("Amount " + amount + " is out of range for transaction type " + transactionType);
                }

                LocalDateTime now = LocalDateTime.now();
                PlacementApproval placementApproval = PlacementApproval.builder()
                        .approvalStatus(Pending)
                        .inputerId(inputId)
                        .inputDate(now)
                        .inputIPAddress(inputIPAddress)
                        .approverId(null)
                        .approveDate(null)
                        .approveIPAddress(null)
                        .imCode(placementData.getImCode())
                        .imName(placementData.getImName())
                        .fundCode(placementData.getFundCode())
                        .fundName(placementData.getFundName())
                        .placementBankCode(placementData.getPlacementBankCode())
                        .placementBankName(placementData.getPlacementBankName())
                        .placementBankCashAccountName(placementData.getPlacementBankCashAccountName())
                        .placementBankCashAccountNo(placementData.getPlacementBankCashAccountNo())
                        .currency(placementData.getCurrency())
                        .principle(amount)
                        .placementDate(placementData.getPlacementDate())
                        .referenceNo(placementData.getReferenceNo())
                        .siReferenceId(placementData.getSiReferenceId())
                        .accountDebitNo(placementData.getAccountDebitNo())
                        .biCode(placementData.getBiCode())
                        .description(request.getDescription())
                        .placementType(placementData.getPlacementType())
                        .placementProcessType(PLACEMENT_TYPE_SINGLE)
                        .placementTransferType(transactionType)
                        .referenceId(GenerateUniqueKeyUtil.generateReferenceId())
                        .inquiryReferenceId(null)
                        .ncbsStatus(null)
                        .ncbsResponseCode(null)
                        .ncbsResponseMessage(null)
                        .payUserRefNo(null)
                        .branchCode(placementData.getBranchCode())
                        .build();

                savePlacementApprovalDataSingle(placementApproval, placementData);

                totalSuccess++;
            } catch (Exception e) {
                totalFailed++;
                errorMessageDTOList.add(new ErrorMessageDTO(request.getId().toString(), Collections.singletonList("Error saving placement approval: " + e.getMessage())));
            }
        }

        return CreatePlacementResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }

    private void savePlacementApprovalDataSingle(PlacementApproval placementApproval, PlacementData placementDataSource) {
        Boolean savingCheck = placementDataRepository.findBySiReferenceIdAndPlacementApprovalId(placementDataSource.getSiReferenceId(), "");
        if (savingCheck) {
            PlacementApproval save = placementApprovalRepository.save(placementApproval);

            // update PlacementData table, to set placementApprovalId and placementApprovalStatus fields
            PlacementData placementDataUpdated = placementDataService.updatePlacementApprovalIdAndStatus(
                    placementDataSource.getId(), save.getId(), save.getApprovalStatus().getStatus()
            );
            log.info("[Single] Placement deposit approval id: {}, placement data id: {}", placementDataUpdated.getPlacementApprovalId(), placementDataUpdated.getId());
        } else {
            log.info("[Single] Placement deposit for approval id: {} and placement data id: {} already saved by another session.", placementDataSource.getPlacementApprovalId(), placementDataSource.getId());
        }
    }

    private static void validateBulkData(List<PlacementData> placementDataList) throws InconsistentDataException {
        if (placementDataList == null || placementDataList.isEmpty()) {
            throw new IllegalArgumentException("Daftar PlacementData tidak boleh kosong.");
        }

        String referenceFundCode = placementDataList.get(0).getFundCode();
        String referencePlacementBankCashAccountNo = placementDataList.get(0).getPlacementBankCashAccountNo();
        String referencePlacementBankCashAccountName = placementDataList.get(0).getPlacementBankCashAccountName();

        for (PlacementData data : placementDataList) {
            if (!referenceFundCode.equals(data.getFundCode())
                    || !referencePlacementBankCashAccountNo.equals(data.getPlacementBankCashAccountNo())
                    || !referencePlacementBankCashAccountName.equals(data.getPlacementBankCashAccountName())
            ) {
                throw new InconsistentDataException("Terdapat perbedaan pada fundCode atau placementBankCashAccountNo atau placementBankCashAccountName: " +
                        "fundCode = " + data.getFundCode() + ", placementBankCashAccountNo = " + data.getPlacementBankCashAccountNo() +
                        ", placementBankCashAccountName = " + data.getPlacementBankCashAccountName());
            }
        }
    }

    private static String validateTransferTypeEnum(String name) {
        TransferPlacementType transferPlacementType = TransferPlacementType.fromTransferTypeName(name);
        return transferPlacementType.getTransferTypeName();
    }

}
