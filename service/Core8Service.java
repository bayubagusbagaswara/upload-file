package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType8DTO;

import java.util.List;

public interface Core8Service {

    List<CoreType8DTO> calculate(String category, String type, String monthYear);

}
