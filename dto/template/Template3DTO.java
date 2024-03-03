package com.services.billingservice.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template3DTO {

    private String transactionHandlingValueFrequency;
    private String transactionHandlingFee;
    private String transactionHandlingAmountDue;

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String totalAmountBeforeTax;

    private String vatFee;
    private String vatAmountDue;

    private String totalAmountAfterTax;
}
