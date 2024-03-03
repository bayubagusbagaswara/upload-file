package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType9DTO {

    private String transactionHandlingValueFrequency;
    private String transactionHandlingFee;
    private String transactionHandlingAmountDue;

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String totalAmountDueBeforeTax;

    private String vatFee;
    private String vatAmountDue;

    private String totalAmountDueAfterTax;
}
