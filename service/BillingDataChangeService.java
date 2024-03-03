package com.services.billingservice.service;

import com.services.billingservice.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillingDataChangeService {

    ResponseEntity<ResponseDTO<Object>> getPendingData();

    ResponseEntity<ResponseDTO<Object>> approveDataChange(Map<String, List<String>> idList);

    ResponseEntity<ResponseDTO<Object>> rejectDataChange(Map<String, List<String>> idList);

    ResponseEntity<ResponseDTO<Object>> getDataBeforeAfter(String id);

}
