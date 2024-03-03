package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType1DTO {

    private String billingNumber;

    private String billingTemplate;

    private String category;

    private String type;

    private String aid;

    private String period;

    private String transactionHandlingValueFrequency;

    private String transactionHandlingFee;

    private String transactionHandlingAmountDue;

    private String safekeepingValueFrequency;

    private String safekeepingFee;

    private String safekeepingAmountDue;

    private String totalAmountBeforeVAT;

    private String vatFee;

    private String vatAmountDue;

    private String totalAmountDue;

}
