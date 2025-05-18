package com.services.billingservice.service.placement.impl;

import com.services.billingservice.dto.placement.bifastpaymentstatus.BiFastPaymentStatusRequest;
import com.services.billingservice.dto.placement.bifastpaymentstatus.BiFastPaymentStatusResponse;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferRequest;
import com.services.billingservice.dto.placement.credittransfer.CreditTransferResponse;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountRequest;
import com.services.billingservice.dto.placement.inquiryaccount.InquiryAccountResponse;
import com.services.billingservice.dto.placement.ncbsrequest.CreateNCBSRequest;
import com.services.billingservice.dto.placement.overbookingcasa.*;
import com.services.billingservice.dto.placement.transfersknrtgs.*;
import com.services.billingservice.exception.GeneralException;
import com.services.billingservice.exception.placement.CustomTimeoutException;
import com.services.billingservice.exception.placement.ReferenceIdAlreadyExists;
import com.services.billingservice.model.placement.NCBSRequest;
import com.services.billingservice.repository.placement.NCBSRequestRepository;
import com.services.billingservice.service.placement.NCBSRequestService;
import com.services.billingservice.service.placement.MiddlewareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.undo.CannotUndoException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class NCBSRequestServiceImpl implements NCBSRequestService {

    private final NCBSRequestRepository ncbsRequestRepository;
    private final MiddlewareService middlewareService;

    private boolean isReferenceIdAlreadyExists(String referenceId) {
        log.info("Check the existing NCBS Request with the reference id: {}", referenceId);
        return ncbsRequestRepository.existsByReferenceId(referenceId);
    }

    @Transactional
    @Override
    public NCBSRequest save(CreateNCBSRequest createNCBSRequest) {
        log.info("[NCBS Request] Start save to the database: {}", createNCBSRequest);

        if (isReferenceIdAlreadyExists(createNCBSRequest.getReferenceId())) {
            throw new ReferenceIdAlreadyExists("Reference Id already exists : " + createNCBSRequest.getReferenceId());
        }

        NCBSRequest ncbsRequest = NCBSRequest.builder()
                .createdDate(createNCBSRequest.getCreatedDate())
                .placementId(createNCBSRequest.getPlacementId())
                .siReferenceId(createNCBSRequest.getSiReferenceId())
                .placementType(createNCBSRequest.getPlacementType())
                .placementProcessType(createNCBSRequest.getPlacementProcessType())
                .placementTransferType(createNCBSRequest.getPlacementTransferType())
                .requestJson(createNCBSRequest.getRequestJson())
                .referenceId(createNCBSRequest.getReferenceId())
                .serviceType(createNCBSRequest.getServiceType())
                .payUserRefNo(createNCBSRequest.getPayUserRefNo())
                .build();

        NCBSRequest save = ncbsRequestRepository.save(ncbsRequest);
        log.info("[NCBS Request] Successfully save with id: {}", save.getId());
        return save;
    }

    @Override
    public InquiryAccountResponse inquiryAccount(InquiryAccountRequest inquiryAccountRequest, String externalId) {
        log.info("[Inquiry Account] Start - ExternalId: {}", externalId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();
        try {
            InquiryAccountResponse response = middlewareService.inquiryAccount(inquiryAccountRequest, externalId)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res ->
                            log.info("[Inquiry Account] Success - ExternalId: {} - Duration: {}ms",
                                    externalId, System.currentTimeMillis() - startTime))
                    .doOnError(e ->
                            log.error("[Inquiry Account] Error during execution - ExternalId: {}",
                                    externalId, e))
                    .doFinally(signalType ->
                            log.debug("[Inquiry Account] Operation signal - ExternalId: {} - Signal: {}",
                                    externalId, signalType))
                    .block(timeout);
            log.info("[Inquiry Account] Success - ExternalId: {}", externalId);
            return response;
        } catch (WebClientResponseException e) {
            log.error("[Inquiry Account] Server Error - ExternalId: {} - Status: {} - Body: {}",
                    externalId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Inquiry Account] Server error: " + e.getStatusCode());
        } catch (IllegalStateException e) {
            log.error("[Inquiry Account] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Inquiry Account] Timeout on blocking read for " + timeout.get(ChronoUnit.SECONDS) + " second");
        } catch (Exception e) {
            log.error("[Inquiry Account] Failed - ExternalId: {} - Error: {}",
                    externalId, e.getMessage(), e);
            throw new GeneralException("[Inquiry Account] Request failed: " + e.getMessage());
        } finally {
            log.info("[Inquiry Account] Completed - ExternalId: {} - Total Duration: {}ms",
                    externalId, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public CreditTransferResponse creditTransfer(String referenceId, CreditTransferRequest creditTransferRequest) {
        log.info("[Credit Transfer] Start - ReferenceId: {}", referenceId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();
        try {
            CreditTransferResponse response = middlewareService.creditTransferBiFast(referenceId, creditTransferRequest)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res ->
                            log.info("[Credit Transfer] Success - ReferenceId: {} - Duration: {}ms",
                                    referenceId, System.currentTimeMillis() - startTime))
                    .doOnError(e ->
                            log.error("[Credit Transfer] Error during execution - ReferenceId: {}",
                                    referenceId, e))
                    .doFinally(signalType ->
                            log.debug("[Credit Transfer] Operation signal - ReferenceId: {} - Signal: {}",
                                    referenceId, signalType))
                    .block(timeout);
            log.info("[Credit Transfer] Success - ReferenceId: {}", referenceId);
            return response;
        } catch (WebClientResponseException e) {
            log.error("[Credit Transfer] Server error - ReferenceId: {} - Status: {} - Body: {}",
                    referenceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Credit Transfer] Server error: " + e.getStatusCode());
        } catch (IllegalStateException e) {
            log.error("[Credit Transfer] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Credit Transfer] Timeout on blocking read for " + timeout.get(ChronoUnit.SECONDS) + " second");
        } catch (Exception e) {
            log.error("[Credit Transfer] Failed - ReferenceId: {} - Error: {}",
                    referenceId, e.getMessage(), e);
            throw new GeneralException("[Credit Transfer] Request failed: " + e.getMessage());
        } finally {
            log.info("[Credit Transfer] Completed - ReferenceId: {} - Total Duration: {}ms",
                    referenceId, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public OverbookingCasaResponse overbookingCasa(String referenceId, OverbookingCasaRequest request) {
        log.info("[Overbooking Casa] Start - ReferenceId: {}", referenceId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();
        try {
            return middlewareService.overbookingCasa(referenceId, request)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res ->
                            log.info("[Overbooking Casa] Success - ReferenceId: {} - Duration: {}ms",
                                    referenceId, System.currentTimeMillis() - startTime))
                    .doOnError(e ->
                            log.error("[Overbooking Casa] Error during execution - ReferenceId: {}",
                                    referenceId, e))
                    .doFinally(signalType ->
                            log.debug("[Overbooking Casa] Operation signal - ReferenceId: {} - Signal: {}",
                                    referenceId, signalType))
                    .block(timeout);
        } catch (WebClientResponseException e) {
            log.error("[Overbooking Casa] Server Error - ReferenceId: {} - Status: {} - Body: {}",
                    referenceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Overbooking Casa] Server error: " + e.getStatusCode());
        } catch (IllegalStateException e) {
            log.error("[Overbooking Casa] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Overbooking Casa] Timeout on blocking read for " + timeout.get(ChronoUnit.SECONDS) + " second");
        } catch (Exception e) {
            log.error("[Overbooking Casa] Failed - ReferenceId: {} - Error: {}",
                    referenceId, e.getMessage(), e);
            throw new GeneralException("[Overbooking Casa] Request failed: " + e.getMessage());
        } finally {
            log.info("[Overbooking Casa] Completed - ReferenceId: {} - Total Duration: {}ms",
                    referenceId, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public TransferSknRtgsResponse transferSknRtgs(String referenceId, TransferSknRtgsRequest transferSknRtgsRequest) {
        log.info("[Transfer Skn Rtgs] Start - ReferenceId: {}", referenceId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();
        try {
            TransferSknRtgsResponse response = middlewareService.transferSknRtgs(referenceId, transferSknRtgsRequest)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res ->
                            log.info("[Transfer Skn Rtgs] Success - ReferenceId: {} - Duration: {}ms",
                                    referenceId, System.currentTimeMillis() - startTime))
                    .doOnError(e ->
                            log.error("[Transfer Skn Rtgs] Error during execution - ReferenceId: {}",
                                    referenceId, e))
                    .doFinally(signalType ->
                            log.debug("[Transfer Skn Rtgs] Operation signal - ReferenceId: {} - Signal: {}",
                                    referenceId, signalType))
                    .block(timeout);
            log.info("[Transfer Skn Rtgs] Success - ReferenceId: {}", referenceId);
            return response;
        } catch (WebClientResponseException e) {
            log.error("[Transfer Skn Rtgs] Server Error - ReferenceId: {} - Status: {} - Body: {}",
                    referenceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Transfer Skn Rtgs] Server error: " + e.getStatusCode());
        } catch (IllegalStateException e) {
            log.error("[Transfer Skn Rtgs] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Transfer Skn Rtgs] Timeout on blocking read for " + timeout.get(ChronoUnit.SECONDS) + " second");
        } catch (Exception e) {
            log.error("[Transfer Skn Rtgs] Failed - ReferenceId: {} - Error {}",
                    referenceId, e.getMessage(), e);
            throw new GeneralException("[Transfer Skn Rtgs] Request failed: " + e.getMessage());
        } finally {
            log.info("[Transfer Skn Rtgs] Completed - ReferenceId: {} - Total Duration: {}ms",
                    referenceId, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public BiFastPaymentStatusResponse paymentStatus(BiFastPaymentStatusRequest biFastPaymentStatusRequest, String externalId) {
        log.info("[Payment Status] Start - ExternalId: {}", externalId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();
        try {
            BiFastPaymentStatusResponse response = middlewareService.paymentStatus(biFastPaymentStatusRequest, externalId)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res ->
                            log.info("[Payment Status] Success - ExternalId: {} - Duration: {}ms",
                                    externalId, System.currentTimeMillis() - startTime))
                    .doOnError(e ->
                            log.error("[Payment Status] Error during execution - ExternalId: {}",
                                    externalId, e))
                    .doFinally(signalType ->
                            log.debug("[Payment Status] Operation signal - ExternalId: {} - Signal: {}",
                                    externalId, signalType))
                    .block(timeout);
            log.info("[Payment Status] Success - ExternalId: {}", externalId);
            return response;
        } catch (WebClientResponseException e) {
            log.error("[Payment Status] Server Error - ExternalId: {} - Status: {} - Body: {}",
                    externalId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Payment Status] Server error: " + e.getStatusCode());
        } catch (IllegalStateException e) {
            log.error("[Payment Status] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Payment Status] Timeout on blocking read for " + timeout.get(ChronoUnit.SECONDS) + " second");
        } catch (Exception e) {
            log.error("[Payment Status] Failed - ExternalId: {} - Error: {}",
                    externalId, e.getMessage(), e);
            throw new GeneralException("[Payment Status] Request failed: " + e.getMessage());
        } finally {
            log.info("[Payment Status] Completed - ExternalId: {} - Total Duration: {}ms",
                    externalId, System.currentTimeMillis() - startTime);
        }
    }

}
