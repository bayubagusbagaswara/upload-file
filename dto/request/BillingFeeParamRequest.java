package com.services.billingservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BillingFeeParamRequest {

    private Long id;

    private String feeCode;

    private String feeName;

    private double value;

    private String description;
}
