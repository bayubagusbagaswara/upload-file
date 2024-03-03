package com.services.billingservice.service;

import com.services.billingservice.dto.request.CreateNasabahTransferAssetRequest;
import com.services.billingservice.dto.request.UpdateNasabahTransferAssetRequest;
import com.services.billingservice.dto.response.NasabahTransferAssetDTO;

import java.util.List;

public interface BillingNasabahTransferAssetService {

    NasabahTransferAssetDTO create(CreateNasabahTransferAssetRequest request);

    NasabahTransferAssetDTO getById(String id);

    NasabahTransferAssetDTO getBySecurityCode(String securityCode);

    List<NasabahTransferAssetDTO> getAll();

    NasabahTransferAssetDTO updateById(String id, UpdateNasabahTransferAssetRequest request);

    NasabahTransferAssetDTO updateBySecurityCode(String securityCode, UpdateNasabahTransferAssetRequest request);

    String deleteById(String id);

    String deleteBySecurityCode(String securityCode);

}
