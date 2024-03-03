package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType7DTO;

import java.util.List;

public interface Core7Service {

    List<CoreType7DTO> calculate(String category, String type, String monthYear);

}
