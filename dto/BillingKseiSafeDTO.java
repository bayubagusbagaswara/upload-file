package com.services.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKseiSafeDTO {

    private Long id;

    private String createdDate;

    private String feeDescription;

    private String feeAccount;

    private BigDecimal amountFee;

}
