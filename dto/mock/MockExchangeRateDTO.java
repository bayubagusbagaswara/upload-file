package com.services.billingservice.dto.mock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockExchangeRateDTO {

    private String id;

    private String date;

    private String currency;

    private String value;

}
