package com.services.billingservice.service.mock;

import com.services.billingservice.dto.mock.MockExchangeRateDTO;

import java.util.List;

public interface MockExchangeRateService {

    String create();

    MockExchangeRateDTO getByCurrencyAndDate(String currency, String monthYear);

    List<MockExchangeRateDTO> getAll();
}
