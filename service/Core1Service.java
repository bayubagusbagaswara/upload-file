package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType1DTO;

import java.util.List;

public interface Core1Service {

    List<CoreType1DTO> calculate(String category, String type, String monthYear);

    String calculate1(String category, String type, String monthYear);

    String generateFilePdf(String category, String type, String monthYear);

}
