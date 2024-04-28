package com.services.billingservice.service.impl;

import com.services.billingservice.dto.datachange.BillingDataChangeDTO;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingDataChange;
import com.services.billingservice.model.base.Approvable;
import com.services.billingservice.repository.BillingDataChangeRepository;
import com.services.billingservice.service.BillingDataChangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingDataChangeServiceImpl implements BillingDataChangeService {

    private final BillingDataChangeRepository billingDataChangeRepository;

    @Override
    public void save(Approvable approvable, String entityClassName, String jsonDataBefore, String jsonDataAfter) {
        log.info("JSON Data Before: {}", jsonDataBefore);
        log.info("JSON Data After: {}", jsonDataAfter);

        // save to entity Billing Data Change
        BillingDataChange billingDataChange = BillingDataChange.builder()
                .build();
    }

    @Override
    public String createDataChange(BillingDataChangeDTO dataChangeDTO) {
        // map to Billing Data Change
        BillingDataChange billingDataChange = BillingDataChange.builder()
                .approvalStatus(dataChangeDTO.getApprovalStatus())
                .approveDate(dataChangeDTO.getApproveDate())
                .approverId(dataChangeDTO.getApproverId())
                .approverIPAddress(dataChangeDTO.getApproverIPAddress())
                .tableName(dataChangeDTO.getTableName())
                .jsonDataBefore(dataChangeDTO.getJsonDataBefore())
                .jsonDataAfter(dataChangeDTO.getJsonDataAfter())
                .build();

        billingDataChangeRepository.save(billingDataChange);
        return "Successfully create data change with id: " + billingDataChange.getId();
    }

    @Override
    public List<BillingDataChange> getAll() {
        return billingDataChangeRepository.findAll();
    }

    @Override
    public BillingDataChangeDTO getById(String id) {
        BillingDataChange billingDataChange = billingDataChangeRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("Billing Data change with ID '" + id + "' not found"));
        return mapToDTO(billingDataChange);
    }

    private static BillingDataChangeDTO mapToDTO(BillingDataChange billingDataChange) {
        return BillingDataChangeDTO.builder()
                .approvalStatus(billingDataChange.getApprovalStatus())
                .approveDate(billingDataChange.getApproveDate())
                .approverId(billingDataChange.getApproverId())
                .approverIPAddress(billingDataChange.getApproverIPAddress())
                .inputDate(billingDataChange.getInputDate())
                .inputerId(billingDataChange.getInputerId())
                .jsonDataBefore(billingDataChange.getJsonDataBefore())
                .jsonDataAfter(billingDataChange.getJsonDataAfter())
                .build();
    }

}
