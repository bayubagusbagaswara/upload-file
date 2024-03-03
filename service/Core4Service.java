package com.services.billingservice.service;

import com.services.billingservice.dto.core.CoreType4DTO;

import java.util.List;
import java.util.Map;

public interface Core4Service {

    List<CoreType4DTO> calculate(
            String category,
            String type,
            String monthYear
    );

    Map<String, List<Object>> calculateTest(
            String category,
            String type,
            String monthYear
    );

    String calculate1(
            String category,
            String type,
            String monthYear
    );

}
