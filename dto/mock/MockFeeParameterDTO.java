package com.services.billingservice.dto.mock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockFeeParameterDTO {

    private String id;

    private String name;

    private String description;

    private String value;

    private String currency;

}
