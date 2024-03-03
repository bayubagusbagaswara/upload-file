package com.services.billingservice.service.impl;

import com.services.billingservice.dto.request.CreateNasabahTransferAssetRequest;
import com.services.billingservice.dto.request.UpdateNasabahTransferAssetRequest;
import com.services.billingservice.dto.response.NasabahTransferAssetDTO;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingNasabahTransferAsset;
import com.services.billingservice.repository.BillingNasabahTransferAssetRepository;
import com.services.billingservice.service.BillingNasabahTransferAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingNasabahTransferAssetServiceImpl implements BillingNasabahTransferAssetService {

    private final BillingNasabahTransferAssetRepository nasabahTransferAssetRepository;

    @Override
    public NasabahTransferAssetDTO create(CreateNasabahTransferAssetRequest request) {
        String securityCode = request.getSecurityCode();
        String clientName = request.getClientName();
        double amount = request.getAmount();
        String effectiveDate = request.getEffectiveDate();

        BillingNasabahTransferAsset nasabahTransferAsset = BillingNasabahTransferAsset.builder()
                .securityCode(securityCode)
                .clientName(clientName)
                .amount(amount)
                .effectiveDate(effectiveDate)
                .build();

//        nasabahTransferAsset.setCreatedAt(LocalDateTime.now());

        BillingNasabahTransferAsset dataSaved = nasabahTransferAssetRepository.save(nasabahTransferAsset);
        return mapToDTO(dataSaved);
    }

    @Override
    public NasabahTransferAssetDTO getById(String id) {
        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findByIdAndIsDeletedFalse(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Data not found"));
        return mapToDTO(nasabahTransferAsset);
    }

    @Override
    public NasabahTransferAssetDTO getBySecurityCode(String securityCode) {
        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findBySecurityCodeAndIsDeletedFalse(securityCode)
                .orElseThrow(() -> new DataNotFoundException("Data not found"));
        return mapToDTO(nasabahTransferAsset);
    }

    @Override
    public List<NasabahTransferAssetDTO> getAll() {
        List<BillingNasabahTransferAsset> nasabahTransferAssets = nasabahTransferAssetRepository.findAllAndIsDeletedFalse();
        return mapToDTOList(nasabahTransferAssets);
    }

    @Override
    public NasabahTransferAssetDTO updateById(String id, UpdateNasabahTransferAssetRequest request) {
        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findByIdAndIsDeletedFalse(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        nasabahTransferAsset.setSecurityCode(request.getSecurityCode());
        nasabahTransferAsset.setClientName(request.getClientName());
        nasabahTransferAsset.setAmount(request.getAmount());
        nasabahTransferAsset.setEffectiveDate(request.getEffectiveDate());
//        nasabahTransferAsset.setsetUpdatedAt(LocalDateTime.now());

        BillingNasabahTransferAsset dataSaved = nasabahTransferAssetRepository.save(nasabahTransferAsset);
        return mapToDTO(dataSaved);
    }

    @Override
    public NasabahTransferAssetDTO updateBySecurityCode(String securityCode, UpdateNasabahTransferAssetRequest request) {
        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findBySecurityCodeAndIsDeletedFalse(securityCode)
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        nasabahTransferAsset.setSecurityCode(request.getSecurityCode());
        nasabahTransferAsset.setClientName(request.getClientName());
        nasabahTransferAsset.setAmount(request.getAmount());
        nasabahTransferAsset.setEffectiveDate(request.getEffectiveDate());

        BillingNasabahTransferAsset dataSaved = nasabahTransferAssetRepository.save(nasabahTransferAsset);
        return mapToDTO(dataSaved);
    }

    @Override
    public String deleteById(String id) {
        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findByIdAndIsDeletedFalse(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        nasabahTransferAsset.setDeleted(true);

        BillingNasabahTransferAsset dataSaved = nasabahTransferAssetRepository.save(nasabahTransferAsset);

        return "Successfully delete nasabah transfer asset with id : " + dataSaved.getId();
    }

    @Override
    public String deleteBySecurityCode(String securityCode) {
        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findBySecurityCodeAndIsDeletedFalse(securityCode)
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        nasabahTransferAsset.setDeleted(true);

        BillingNasabahTransferAsset dataSaved = nasabahTransferAssetRepository.save(nasabahTransferAsset);

        return "Successfully delete nasabah transfer asset with security code : " + dataSaved.getId();
    }

    private NasabahTransferAssetDTO mapToDTO(BillingNasabahTransferAsset nasabahTransferAsset) {
        return NasabahTransferAssetDTO.builder()
                .id(String.valueOf(nasabahTransferAsset.getId()))
                .securityCode(nasabahTransferAsset.getSecurityCode())
                .clientName(nasabahTransferAsset.getClientName())
                .amount(nasabahTransferAsset.getAmount())
                .effectiveDate(nasabahTransferAsset.getEffectiveDate())
//                .createdAt(nasabahTransferAsset.getCreatedAt())
                .isDeleted(nasabahTransferAsset.isDeleted())
                .build();
    }

    private List<NasabahTransferAssetDTO> mapToDTOList(List<BillingNasabahTransferAsset> nasabahTransferAssetList) {
        return nasabahTransferAssetList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

}
