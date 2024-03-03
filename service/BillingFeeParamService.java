package com.services.billingservice.service;

import com.services.billingservice.dto.BillingFeeParamDTO;
import com.services.billingservice.dto.request.BillingFeeParamRequest;
import com.services.billingservice.dto.response.NasabahTransferAssetDTO;
import com.services.billingservice.model.BillingFeeParam;

import java.util.List;

public interface BillingFeeParamService {

    //single maintenance
    BillingFeeParamDTO create(BillingFeeParamRequest request);
    BillingFeeParamDTO getByCode(String code);
    List<BillingFeeParamDTO>getAll();
    BillingFeeParamDTO updateByCode(String code, BillingFeeParamRequest request);

    //upload
    List<BillingFeeParamDTO> upload(List<BillingFeeParamRequest> request);
    List<BillingFeeParamDTO> updateUploadByCode(List<BillingFeeParamRequest> request);


}
