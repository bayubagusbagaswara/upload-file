package com.services.billingservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFeeParamResponse {

    private Long id;

    private String feeCode;

    private String feeName;

    private String value;

    private String description;
}
