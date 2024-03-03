package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType9DTO;

import java.util.List;

public interface Core9Service {

    List<CoreType9DTO> calculate(String category, String type, String monthYear);

}
