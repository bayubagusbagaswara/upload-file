package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType6DTO;

import java.util.List;

public interface Core6Service {

    List<CoreType6DTO> calculate(String category, String type, String monthYear);

    String calculate1(String category, String type, String monthYear);

}
