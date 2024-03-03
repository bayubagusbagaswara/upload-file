package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType10DTO;

import java.util.List;

public interface Core10Service {

    List<CoreType10DTO> calculate(String category, String type, String monthYear);
}
