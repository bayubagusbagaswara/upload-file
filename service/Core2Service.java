package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType2DTO;

import java.util.List;

public interface Core2Service {

    List<CoreType2DTO> calculate(String category, String type, String monthYear);

    String calculate1(String category, String type, String monthYear);

}
