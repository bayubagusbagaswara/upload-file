package com.services.billingservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.enums.ChangeAction;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingDataChange;
import com.services.billingservice.model.BillingNasabahTransferAsset;
import com.services.billingservice.repository.BillingDataChangeRepository;
import com.services.billingservice.repository.BillingNasabahTransferAssetRepository;
import com.services.billingservice.service.BillingDataChangeService;
import com.services.billingservice.utils.UserIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BillingDataChangeServiceImpl implements BillingDataChangeService {


    @Autowired
    private BillingNasabahTransferAssetRepository nasabahTransferAssetRepository;

    @Autowired
    private BillingDataChangeRepository billingDataChangeRepository;

    @Override
    public ResponseEntity<ResponseDTO<Object>> getPendingData() {
        ResponseDTO<Object> responseDto = new ResponseDTO<>();
        try {
            List<BillingDataChange> billingDataChanges = billingDataChangeRepository.searchAllByApprovalStatusPending();
            responseDto.setCode(HttpStatus.OK.value());
            responseDto.setMessage("OK");
            responseDto.setPayload(billingDataChanges);
        } catch (Exception e) {
            responseDto.setCode(HttpStatus.BAD_REQUEST.value());
            responseDto.setMessage("FAILED");
            responseDto.setPayload(e.getMessage());
            e.printStackTrace();
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<ResponseDTO<Object>> approveDataChange(Map<String, List<String>> ids) {
        ResponseDTO<Object> responseDto = new ResponseDTO<>();
        List<String> idList = ids.get("idList");
        System.out.println("MASUK APPROVE DATA CHANGE, LIST: " + idList);

        try {
            ObjectMapper mapper = new ObjectMapper();
            for (String id : idList) {
                BillingDataChange dataChange = billingDataChangeRepository.findById(Long.valueOf(id)).orElse(null);

                if (null != dataChange) {
                    if (BillingNasabahTransferAsset.class.getName().equalsIgnoreCase(dataChange.getEntityClassName())) {
                        BillingNasabahTransferAsset nasabahTransferAsset = nasabahTransferAssetRepository.findById(Long.valueOf(dataChange.getEntityId()))
                                .orElseThrow(() -> new DataNotFoundException("Data not found"));

                        BillingNasabahTransferAsset dto = mapper.readValue(dataChange.getDataChange(), BillingNasabahTransferAsset.class);

                        nasabahTransferAsset.setApprovalStatus(ApprovalStatus.Approved);
                        nasabahTransferAsset.setApproveDate(new Date());
                        nasabahTransferAsset.setApproverId(UserIdUtil.getUser());
                        nasabahTransferAsset.setInputDate(dataChange.getInputDate());
                        nasabahTransferAsset.setInputerId(dataChange.getInputerId());

                        if (dataChange.getAction() == ChangeAction.Edit) {
                            nasabahTransferAsset.setDeleted(dto.isDeleted());
                            nasabahTransferAsset.setId(dto.getId());
                            nasabahTransferAsset.setSecurityCode(dto.getSecurityCode());
                            nasabahTransferAsset.setClientName(dto.getClientName());
                            nasabahTransferAsset.setAmount(dto.getAmount());
                            nasabahTransferAsset.setEffectiveDate(dto.getEffectiveDate());

                            // edit data change, then edit nasabahTransferAsset
                        } else if (dataChange.getAction() == ChangeAction.Delete){
                            // delete data change, then set deleted
                            nasabahTransferAsset.setDeleted(dto.isDeleted());
                        }

                        // save repository
                        nasabahTransferAssetRepository.save(nasabahTransferAsset);
                    }

                    dataChange.setApprovalStatus(ApprovalStatus.Approved);
                    dataChange.setApproveDate(new Date());
                    dataChange.setApproverId(UserIdUtil.getUser());
                    billingDataChangeRepository.save(dataChange);
                } else {
                    // jika dataChange is null
                }
            }

            responseDto.setCode(HttpStatus.OK.value());
            responseDto.setMessage("OK");
            responseDto.setPayload("Save Data Change Success");
        } catch (Exception e) {
            responseDto.setCode(HttpStatus.BAD_REQUEST.value());
            responseDto.setMessage("FAILED");
            responseDto.setPayload(e.getMessage());
            e.printStackTrace();
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseDTO<Object>> rejectDataChange(Map<String, List<String>> ids) {
        ResponseDTO<Object> responseDto = new ResponseDTO<>();
        List<String> idList = ids.get("");
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO<Object>> getDataBeforeAfter(String id) {
        return null;
    }
}
