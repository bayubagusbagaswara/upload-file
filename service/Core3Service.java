package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType3DTO;

import java.util.List;

public interface Core3Service {

    List<CoreType3DTO> calculate(String category, String type, String monthYear);

    String calculate1(String category, String type, String monthYear);
}
