package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType5DTO;

import java.util.List;

public interface Core5Service {

    List<CoreType5DTO> calculate(String category, String type, String monthYear);

    String calculate1(String category, String type, String monthYear);
}
