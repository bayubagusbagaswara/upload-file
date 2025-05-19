package com.services.billingservice.service.placement.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.billingservice.dto.ErrorMessageDTO;
import com.services.billingservice.dto.placement.apiresponse.SubStatusProviderDTO;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountDataDTO;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.ncbsrequest.CreateNCBSRequest;
import com.services.billingservice.dto.placement.ncbsresponse.CreateNCBSResponse;
import com.services.billingservice.dto.placement.overbookingcasa.*;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalDTO;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalResponse;
import com.services.billingservice.dto.placement.placementapproval.PlacementApprovalResult;
import com.services.billingservice.dto.placement.transfersknrtgs.*;
import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.enums.placement.TransactionType;
import com.services.billingservice.enums.placement.TransferPlacementType;
import com.services.billingservice.exception.BadRequestException;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.mapper.placement.PlacementApprovalMapper;
import com.services.billingservice.model.placement.*;
import com.services.billingservice.repository.placement.PlacementDataRepository;
import com.services.billingservice.repository.placement.PlacementApprovalRepository;
import com.services.billingservice.service.placement.*;
import com.services.billingservice.utils.placement.GenerateUniqueKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.services.billingservice.constant.placement.PlacementConstant.*;
import static com.services.billingservice.enums.ApprovalStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlacementApprovalServiceImpl implements PlacementApprovalService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PlacementApprovalRepository placementApprovalRepository;
    private final PlacementDataRepository placementDataRepository;
    private final PlacementApprovalMapper placementApprovalMapper;
    private final NCBSRequestService ncbsRequestService;
    private final NCBSResponseService ncbsResponseService;
    private final ResponseCodeService responseCodeService;
    private final FeeParameterPlacementService feeParameterPlacementService;

    @Transactional
    @Override
    public PlacementApprovalResponse approve(List<Long> placementApprovalIds, String approveId, String approveIPAddress, String statusRun) {
        log.info("Start approve placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementApprovalIds, approveId, approveIPAddress);
        int totalSuccess = 0;
        int totalFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (Long placementApprovalId : placementApprovalIds) {
            try {
                PlacementApproval placementApproval = placementApprovalRepository.findById(placementApprovalId).orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementApprovalId));

                if (RERUN.equalsIgnoreCase(statusRun)) {
                    placementApproval.setReferenceId(GenerateUniqueKeyUtil.generateReferenceId());
                }

                PlacementApprovalResult result = processApproval(placementApproval, approveId, approveIPAddress, errorMessageDTOList);

                if (result.isSuccess()) {
                    totalSuccess++;
                } else {
                    totalFailed++;
                    errorMessageDTOList.addAll(result.getErrorMessages());
                }

            } catch (Exception e) {
                log.error("Error processing placement transfer: {}", e.getMessage(), e);
                errorMessageDTOList.add(new ErrorMessageDTO(
                        placementApprovalId.toString(),
                        Collections.singletonList("System error: " + e.getMessage())
                ));
                totalFailed++;
            }
        }

        return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
    }

    @Transactional
    @Override
    public PlacementApprovalResponse reject(List<Long> placementApprovalIds, String approveId, String approveIPAddress) {
        log.info("Start reject placement approval with id: {}, approveId: {}, and approveIPAddress: {}", placementApprovalIds, approveId, approveIPAddress);
        int totalSuccess = 0;
        int totalFailed = 0;
        List<ErrorMessageDTO> errorMessageDTOList = new ArrayList<>();

        for (Long placementApprovalId : placementApprovalIds) {
            try {
                PlacementApproval placementApproval = placementApprovalRepository.findById(placementApprovalId).orElseThrow(() -> new DataNotFoundException("Placement Approval not found with id: " + placementApprovalId));
                placementApproval.setApprovalStatus(Rejected);
                placementApproval.setApproverId(approveId);
                placementApproval.setApproveIPAddress(approveIPAddress);
                placementApproval.setApproveDate(LocalDateTime.now());

                List<PlacementData> placementDataList = placementDataRepository.findByPlacementApprovalId(String.valueOf(placementApprovalId));

                if (!placementDataList.isEmpty()) {
                    placementDataList.forEach(placementData -> {
                        placementData.setPlacementApprovalId("");
                        placementData.setPlacementApprovalStatus("");
                    });

                    List<PlacementData> savedData = placementDataRepository.saveAll(placementDataList);

                    savedData.forEach(placementData ->
                            log.info("Successfully removed placement approval from placement data id: {}", placementData.getId())
                    );
                } else {
                    log.warn("No placement data found for approval ID: {}", placementApprovalId);
                }

                PlacementApproval save = placementApprovalRepository.save(placementApproval);
                log.info("Save reject placement approval: {}", save);
                totalSuccess++;
            } catch (Exception e) {
                log.error("Error when reject placement: {}", e.getMessage(), e);
                totalFailed++;
            }
        }
        return buildPlacementApprovalResponse(totalSuccess, totalFailed, errorMessageDTOList);
    }

    @Override
    public List<PlacementApprovalDTO> getAllByCurrentDateAndApprovalStatusAndPlacementTransferType(String approvalStatus, String placementTransferType) {
        log.info("Start get all placement approval by current date and approval status: {}", approvalStatus);
        try {
            LocalDate date = LocalDate.now();
            ApprovalStatus approvalStatusEnum = Pending;

            TransferPlacementType transferPlacementType = TransferPlacementType.fromTransferTypeName(placementTransferType);

            if (Approved.getStatus().equalsIgnoreCase(approvalStatus)) {
                approvalStatusEnum = Approved;
            } else if (Rejected.getStatus().equalsIgnoreCase(approvalStatus)) {
                approvalStatusEnum = Rejected;
            }

            List<PlacementApproval> placementApprovalList = placementApprovalRepository.findByPlacementDateAndApprovalStatusAndPlacementTransferType(date, approvalStatusEnum, transferPlacementType.getTransferTypeName());
            return placementApprovalMapper.toDTOList(placementApprovalList);
        } catch (Exception e) {
            log.error("Error when get all placement approval by date and approval status: {}", e.getMessage(), e);
            throw new BadRequestException("Error when get all placement approval by data and approval status: " + e.getMessage());
        }
    }

    @Override
    public PlacementApproval getById(Long id) {
        return placementApprovalRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Placement not found with id: " + id));
    }

    @Override
    public List<PlacementApprovalDTO> getAllByDateAndApprovalStatusIsPending() {
        log.info("Start get all by date and approval status is pending");
        try {
            LocalDate localDate = LocalDate.now();
            List<PlacementApproval> placementApprovalList = placementApprovalRepository.findByPlacementDateAndApprovalStatus(localDate, Pending);
            return placementApprovalMapper.toDTOList(placementApprovalList);
        } catch (Exception e) {
            log.error("Error when get all placement approval by date and approval status pending: {}", e.getMessage(), e);
            throw new BadRequestException("Error when get all placement approval by data and approval status pending: " + e.getMessage());
        }
    }

    private PlacementApprovalResponse buildPlacementApprovalResponse(int totalSuccess, int totalFailed, List<ErrorMessageDTO> errorMessageDTOList) {
        return PlacementApprovalResponse.builder()
                .totalDataSuccess(totalSuccess)
                .totalDataFailed(totalFailed)
                .errorMessageDTOList(errorMessageDTOList)
                .build();
    }

    private String convertObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error serializing object to JSON: {}", e.getMessage(), e);
            return "{\"error\": \"Failed to serialize object\"}";
        }
    }

    private PlacementApprovalResult processApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
        if (PLACEMENT_TYPE_INTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            return processInternalTransfer(placementApproval, approveId, approveIPAddress); // OVERBOOKING
        } else if (PLACEMENT_TYPE_EXTERNAL.equalsIgnoreCase(placementApproval.getPlacementType())) {
            return processExternalTransfer(placementApproval, approveId, approveIPAddress, errorMessageDTOList); // BI-FAST, SKN, RTGS
        }
        log.warn("Unsupported placement type: {}", placementApproval.getPlacementType());
        return PlacementApprovalResult.failed(Collections.singletonList(
                        new ErrorMessageDTO(placementApproval.getSiReferenceId(),
                        Collections.singletonList("Unsupported placement type"))
        ));
    }

    private PlacementApprovalResult processInternalTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress) {
        OverbookingCasaRequest request = createOverbookingCasaRequest(placementApproval);
        saveNCBSRequest(placementApproval, OVERBOOKING_CASA, convertObjectToJson(request));

        OverbookingCasaResponse response = ncbsRequestService.overbookingCasa(placementApproval.getReferenceId(), request);
        saveNCBSResponse(placementApproval, response.getResponseCode(), response.getResponseMessage(), response.getSubStatusProvider(), convertObjectToJson(response), OVERBOOKING_CASA);

        return finalizeApproval(placementApproval, approveId, approveIPAddress, response.getResponseCode(), response.getResponseMessage(), OVERBOOKING_CASA);
    }

    private PlacementApprovalResult processExternalTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress, List<ErrorMessageDTO> errorMessageDTOList) {
        String placementTransferType = placementApproval.getPlacementTransferType();
        if (BI_FAST.equalsIgnoreCase(placementTransferType)) {
            return processBiFastTransfer(placementApproval, approveId, approveIPAddress);
        } else if (SKN.equalsIgnoreCase(placementTransferType) || RTGS.equalsIgnoreCase(placementTransferType)) {
            return processTransferSknRtgs(placementApproval, approveId, approveIPAddress);
        }
        log.warn("Unsupported placement transfer type: {}", placementTransferType);
        return PlacementApprovalResult.builder().success(false).errorMessages(errorMessageDTOList).build();
    }

    private PlacementApprovalResult processBiFastTransfer(PlacementApproval placementApproval, String approveId, String approveIPAddress) {
        InquiryAccountRequest inquiryAccountRequest = createInquiryAccountRequest(placementApproval);

        placementApproval.setInquiryReferenceId(GenerateUniqueKeyUtil.generateReferenceId());
        saveNCBSRequest(placementApproval, INQUIRY_ACCOUNT, convertObjectToJson(inquiryAccountRequest));

        InquiryAccountResponse inquiryAccountResponse = ncbsRequestService.inquiryAccount(inquiryAccountRequest, placementApproval.getInquiryReferenceId());
        saveNCBSResponse(placementApproval, inquiryAccountResponse.getResponseCode(), inquiryAccountResponse.getResponseMessage(), inquiryAccountResponse.getSubStatusProvider(), convertObjectToJson(inquiryAccountResponse), INQUIRY_ACCOUNT);

        if (!API_RESPONSE_CODE_SUCCESS.equals(inquiryAccountResponse.getResponseCode())) {
            boolean isInsufficientBalance = checkIfInsufficientBalance(inquiryAccountResponse.getResponseCode());
            placementApproval.setApproverId(approveId);
            placementApproval.setApproveIPAddress(approveIPAddress);
            placementApproval.setApproveDate(LocalDateTime.now());
            placementApproval.setNcbsResponseCode(inquiryAccountResponse.getResponseCode());
            placementApproval.setNcbsResponseMessage(inquiryAccountResponse.getResponseMessage());

            handleFailure(placementApproval); // update placementApproval value, placementApprovalStatus is Approved and ncbsStatus is Failed

            placementApprovalRepository.save(placementApproval);

            updatePlacementData(placementApproval, isInsufficientBalance); // update placementData for placementApprovalStatus is Approved

            return PlacementApprovalResult.failed(Collections.singletonList(
                    new ErrorMessageDTO(placementApproval.getSiReferenceId(),
                            Collections.singletonList("Inquiry failed: " + inquiryAccountResponse.getResponseMessage()))
            ));
        }

        CreditTransferRequest creditTransferRequest = createCreditTransferBiFastRequest(inquiryAccountResponse.getData(), placementApproval);
        saveNCBSRequest(placementApproval, CREDIT_TRANSFER, convertObjectToJson(creditTransferRequest));

        String referenceId = placementApproval.getReferenceId();
        CreditTransferResponse creditTransferResponse = ncbsRequestService.creditTransfer(referenceId, creditTransferRequest);

        String payUserRefNo = creditTransferResponse.getData().getPayUserRefNo();
        placementApproval.setPayUserRefNo(payUserRefNo); // set to the placementApproval object
        log.info("[{}] Reference ID: {}, Pay User Ref No: {}", CREDIT_TRANSFER, referenceId, payUserRefNo);
        saveNCBSResponse(placementApproval, creditTransferResponse.getResponseCode(), creditTransferResponse.getResponseMessage(),
                creditTransferResponse.getSubStatusProvider(), convertObjectToJson(creditTransferResponse), CREDIT_TRANSFER);

        PlacementApprovalResult result = finalizeApproval(placementApproval, approveId, approveIPAddress,
                creditTransferResponse.getResponseCode(), creditTransferResponse.getResponseMessage(), CREDIT_TRANSFER);

        if (!result.isSuccess()) {
            // Add more specific error message if needed
            return PlacementApprovalResult.failed(Collections.singletonList(
                    new ErrorMessageDTO(placementApproval.getSiReferenceId(),
                            Collections.singletonList("Transfer failed: " + creditTransferResponse.getResponseMessage()))
            ));
        }

        return result;
    }

    private PlacementApprovalResult processTransferSknRtgs(PlacementApproval placementApproval, String approveId, String approveIPAddress) {
        TransferSknRtgsRequest request = createTransferSknRtgsRequest(placementApproval);
        saveNCBSRequest(placementApproval, TRANSFER_SKN_RTGS, convertObjectToJson(request));

        TransferSknRtgsResponse response = ncbsRequestService.transferSknRtgs(placementApproval.getReferenceId(), request);
        saveNCBSResponse(placementApproval, response.getResponseCode(), response.getResponseMessage(), response.getSubStatusProvider(), convertObjectToJson(response), TRANSFER_SKN_RTGS);

        return finalizeApproval(placementApproval, approveId, approveIPAddress, response.getResponseCode(), response.getResponseMessage(), TRANSFER_SKN_RTGS);
    }

    private PlacementApprovalResult finalizeApproval(PlacementApproval placementApproval, String approveId, String approveIPAddress, String responseCode, String responseMessage, String processName) {
        boolean isSuccess = API_RESPONSE_CODE_SUCCESS.equals(responseCode);
        boolean isInsufficientBalance = checkIfInsufficientBalance(responseCode);

        // Set common fields for all cases
        placementApproval.setApproverId(approveId);
        placementApproval.setApproveIPAddress(approveIPAddress);
        placementApproval.setApproveDate(LocalDateTime.now());
        placementApproval.setNcbsResponseCode(responseCode);
        placementApproval.setNcbsResponseMessage(responseMessage);

        if (!isSuccess || isInsufficientBalance) {
            handleFailure(placementApproval);
        } else {
            handleSuccess(placementApproval);
        }

        // Save the updated placementApproval
        placementApprovalRepository.save(placementApproval);

        // Update PlacementData table
        updatePlacementData(placementApproval, isInsufficientBalance);

        if (isSuccess) {
            return PlacementApprovalResult.success();
        } else {
            return PlacementApprovalResult.failed(
                    placementApproval.getSiReferenceId(),
                    processName + " failed: " + responseMessage
            );
        }
    }

    private boolean checkIfInsufficientBalance(String responseCode) {
        List<String> insufficientBalanceCodes = responseCodeService.getAllByName(INSUFFICIENT_BALANCE)
                .stream()
                .map(ResponseCode::getNcbsResponseCode)
                .collect(Collectors.toList());
        return insufficientBalanceCodes.contains(responseCode);
    }

    private void handleSuccess(PlacementApproval placementApproval) {
        placementApproval.setApprovalStatus(Approved);
        placementApproval.setNcbsStatus(NCBS_STATUS_SUCCESS);
    }

    private void handleFailure(PlacementApproval placementApproval) {
        placementApproval.setApprovalStatus(Approved);
        placementApproval.setNcbsStatus(NCBS_STATUS_FAILED);
    }

    private void updatePlacementData(PlacementApproval placementApproval, boolean isInsufficientBalance) {
        List<PlacementData> placementDataList = placementDataRepository.findByPlacementApprovalId(placementApproval.getId().toString());
        if (placementDataList != null && !placementDataList.isEmpty()) {
            placementDataList.forEach(placementData -> {
                if (placementData != null) {
                    placementData.setPlacementApprovalStatus(placementApproval.getApprovalStatus().getStatus());
                    if (isInsufficientBalance) {
                        // Set to empty string for insufficient balance case
                        placementData.setPlacementApprovalId("");
                        placementData.setPlacementApprovalStatus("");
                    }
                }
            });
            placementDataRepository.saveAll(placementDataList);
        }
    }

    private InquiryAccountRequest createInquiryAccountRequest(PlacementApproval placementApproval) {
        return InquiryAccountRequest.builder()
                .sttlmAmt(placementApproval.getPrinciple().toPlainString())
                .sttlmCcy("IDR")
                .chargeBearerCode("DEBT")
                .senderBic("BDINIDJA")
                .senderAcctNo(placementApproval.getAccountDebitNo())
                .benefBic(placementApproval.getBiCode())
                .benefAcctNo(placementApproval.getPlacementBankCashAccountNo())
                .purposeTransaction("51002")
                .build();
    }

    private CreditTransferRequest createCreditTransferBiFastRequest(InquiryAccountDataDTO inquiryAccountDataDTO, PlacementApproval placementApproval) {
        FeeParameterPlacement feeParameterPlacement = feeParameterPlacementService.getByTransactionType(placementApproval.getPlacementTransferType());
        log.info("[Credit Transfer] Placement approval: {}", placementApproval);
        return CreditTransferRequest.builder()
                .trxType(CREDIT_TRANSFER)
                .category("01")
                .sttlmAmt(placementApproval.getPrinciple().setScale(0, RoundingMode.DOWN) + ".00")
                .sttlmCcy("IDR")
                .sttlmDate(placementApproval.getPlacementDate().toString()) // format must be yyyy-MM-dd
                .feeAmt(feeParameterPlacement.getAmount().toPlainString()) // fee BiFast
                .chargeBearerCode("DEBT")
                .senderAcctNo(placementApproval.getAccountDebitNo())
                .senderAcctType("SVGS")
                .senderBic("BDINIDJA")
                .benefBic(placementApproval.getBiCode())
                .benefName(inquiryAccountDataDTO.getBenefAcctName())
                .benefId(inquiryAccountDataDTO.getBenefId())
                .benefAcctNo(inquiryAccountDataDTO.getBenefAcctNo())
                .benefAcctType(inquiryAccountDataDTO.getBenefAcctType())
                .proxyType("")
                .proxyValue("")
                .description(placementApproval.getDescription())
                .benefType(inquiryAccountDataDTO.getBenefType())
                .benefResidentStatus(inquiryAccountDataDTO.getBenefResidentStatus())
                .benefCityCode(inquiryAccountDataDTO.getBenefCityCode())
                .purposeTransaction("01")
                .cardNo("")
                .build();
    }

    private OverbookingCasaRequest createOverbookingCasaRequest(PlacementApproval placementApproval) {
        HeaderDTO header = HeaderDTO.builder()
                .codOrgBrn("9999")
                .build();

        AcctIdFromDTO acctIdFrom = AcctIdFromDTO.builder()
                .atmCardNo("")
                .acctIdF(placementApproval.getAccountDebitNo())
                .acctTypeF("20")
                .costCtrF("9207")
                .build();

        AcctIdToDTO acctIdTo = AcctIdToDTO.builder()
                .acctIdT(placementApproval.getPlacementBankCashAccountNo())
                .acctTypeT("20")
                .costCtrT("")
                .build();

        XferInfoDTO xFerInfo = XferInfoDTO.builder()
                .xferAmt(placementApproval.getPrinciple().setScale(0, RoundingMode.DOWN) + "00")
                .xferdesc1(placementApproval.getDescription())
                .xferdesc2(placementApproval.getDescription())
                .build();

        BodyDTO body = BodyDTO.builder()
                .acctIdFrom(acctIdFrom)
                .acctIdTo(acctIdTo)
                .xferInfo(xFerInfo)
                .build();

        return OverbookingCasaRequest.builder()
                .header(header)
                .body(body)
                .build();
    }

    private TransferSknRtgsRequest createTransferSknRtgsRequest(PlacementApproval placementApproval) {
        FeeParameterPlacement feeParameterPlacement = feeParameterPlacementService.getByTransactionType(placementApproval.getPlacementTransferType());
        String transactionType = feeParameterPlacement.getTransactionType();
        String transactionTypeCode;

        try {
            TransactionType type = TransactionType.fromString(transactionType);
            transactionTypeCode = type.getCode();
        } catch (Exception e) {
            throw new DataNotFoundException("Invalid transaction type: " + transactionType);
        }

        List<HeaderDTO> headers = new ArrayList<>();
        headers.add(new HeaderDTO("9999"));

        XferInfoFromDTO xferInfoFrom = XferInfoFromDTO.builder()
                .acctId(placementApproval.getAccountDebitNo())
                .acctType("20")
                .acctCur("360")
                .build();

        BankInfoDTO bankInfo = BankInfoDTO.builder()
                .biCode(placementApproval.getBranchCode())
                .cocCode(null)
                .name(placementApproval.getPlacementBankName()) // dari data placement
                .build();

        BdiXferBeneficieryDTO bdiXferBeneficiery = BdiXferBeneficieryDTO.builder()
                .bdiBenfID("")
                .bdiBenfAcct(placementApproval.getPlacementBankCashAccountNo()) // account penerima dari S-Invest
                .bdiBenfName(placementApproval.getPlacementBankCashAccountName()) // account name dari S-Invest
                .bdiBenfAddress("")
                .bdiBenStatus("Y")
                .bdiBenCitizen("Y")
                .bankInfo(bankInfo)
                .build();

        BdiXferMemoDTO bdiXferMemo = BdiXferMemoDTO.builder()
                .bdiFrMemo1(placementApproval.getDescription())
                .bdiFrMemo2("")
                .bdiToMemo1(placementApproval.getDescription())
                .bdiToMemo2("")
                .build();

        TransInfoDTO transInfo = TransInfoDTO.builder()
                .trn("")
                .bdiFeeBearerType("OUR") //need confirm
                .build();

        BdiXferXFOffDTO bdiXferXFOff = BdiXferXFOffDTO.builder()
                .bdiXferAmtFrm(
                        padWithLeadingZeros(placementApproval.getPrinciple().setScale(0, RoundingMode.DOWN) + "00")
                )
                .bdiXferAmtFrmLCE(
                        padWithLeadingZeros(placementApproval.getPrinciple().setScale(0, RoundingMode.DOWN) + "00")
                )
                .bdiXferAmtTo(
                        padWithLeadingZeros(placementApproval.getPrinciple().setScale(0, RoundingMode.DOWN) + "00")

                )
                .bdiXferAmtToLCE(
                        padWithLeadingZeros( placementApproval.getPrinciple().setScale(0, RoundingMode.DOWN) + "00")
                )
                .bdiXferType(transactionTypeCode) // 3 = SKN, 1 = RTGS
                .bdiXferCurCode("360")
                .bdiXRateAmt(null)
                .bdiStdRateAmt(null)
                .bdiXReffNumber(placementApproval.getSiReferenceId())
                .bdiXferBeneficiery(bdiXferBeneficiery)
                .bdiXferCostCtr("9207")
                .bdiXferCodTxnBrn("")
                // SKN 2.900, RTGS 30.000
                .bdiFeeAmt(
                        padWithLeadingZeros( feeParameterPlacement.getAmount().setScale(0, RoundingMode.DOWN) + "00")
                )
                .bdiFeeAmtLCE(
                        padWithLeadingZeros( feeParameterPlacement.getAmount().setScale(0, RoundingMode.DOWN) + "00")
                )
                .bdiFeeExchangeRate(null)
                .bdiFeeProcIr("S")
                .bdiXferMemo(bdiXferMemo)
                .transInfo(transInfo)
                .lldInfo("ID015") // need confirm
                .build();

        return TransferSknRtgsRequest.builder()
                .headers(headers)
                .xferInfoFrom(xferInfoFrom)
                .bdiXferXFOff(bdiXferXFOff)
                .build();
    }

    private void saveNCBSRequest(PlacementApproval placementApproval, String serviceType, String requestJson) {
        CreateNCBSRequest createNCBSRequest = CreateNCBSRequest.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId().toString())
                .siReferenceId(placementApproval.getSiReferenceId())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .requestJson(requestJson)
                .referenceId(INQUIRY_ACCOUNT.equals(serviceType) ? placementApproval.getInquiryReferenceId() : placementApproval.getReferenceId())
                .serviceType(serviceType)
                .build();

        NCBSRequest ncbsRequest = ncbsRequestService.save(createNCBSRequest);
        log.info("[NCBS Request] Successfully saved with id: {}", ncbsRequest.getId());
    }

    private void saveNCBSResponse(PlacementApproval placementApproval, String responseCode, String responseMessage, SubStatusProviderDTO subStatusProvider, String responseJson, String serviceType) {
        boolean isSuccess = API_RESPONSE_CODE_SUCCESS.equals(responseCode);
        CreateNCBSResponse createNCBSResponse = CreateNCBSResponse.builder()
                .createdDate(LocalDateTime.now())
                .placementId(placementApproval.getId())
                .siReferenceId(placementApproval.getSiReferenceId())
                .placementType(placementApproval.getPlacementType())
                .placementProcessType(placementApproval.getPlacementProcessType())
                .placementTransferType(placementApproval.getPlacementTransferType())
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .providerSystem(subStatusProvider.getProviderSystem())
                .statusCode(subStatusProvider.getStatusCode())
                .statusMessage(subStatusProvider.getStatusMessage())
                .responseJson(responseJson)
                .ncbsStatus(isSuccess ? NCBS_STATUS_SUCCESS : NCBS_STATUS_FAILED)
                .referenceId(INQUIRY_ACCOUNT.equals(serviceType) ? placementApproval.getInquiryReferenceId() : placementApproval.getReferenceId())
                .payUserRefNo(placementApproval.getPayUserRefNo())
                .serviceType(serviceType)
                .placementDate(placementApproval.getPlacementDate())
                .build();

        NCBSResponse ncbsResponse = ncbsResponseService.save(createNCBSResponse);
        log.info("[NCBS Response] Successfully saved with id: {}", ncbsResponse.getId());
    }

    @Override
    public List<String> findAllTransferType() {
        log.info("Start get all transfer type placement approval");
        return placementApprovalRepository.findAllTransferType();
    }

    @Override
    public List<PlacementApprovalDTO> getAllByDate(String date) {
        log.info("Start get all by date: {}", date);
        LocalDate localDate = LocalDate.parse(date);
        List<PlacementApproval> placementApprovalList = placementApprovalRepository.findAllByDate(localDate);
        return placementApprovalMapper.toDTOList(placementApprovalList);
    }

    public static String padWithLeadingZeros(String numberStr) {
        try {
            long number = Long.parseLong(numberStr);
            return String.format("%015d", number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format", e);
        }
    }

}
