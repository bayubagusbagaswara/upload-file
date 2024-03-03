package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType8DTO {

    private String exchangeRate; // 15.510,00
    private String exchangeRatePeriod; // 30 Nov 2023

    private String administrationSetUpItem;
    private String administrationSetUpFee;
    private String administrationAmountDue;

    private String signingRepresentationItem;
    private String signingRepresentationFee;
    private String signingRepresentationAmountDue;

    private String securityAgentItem;
    private String securityAgentFee;
    private String securityAgentAmountDue;

    private String transactionHandlingItem;
    private String transactionHandlingFee;
    private String transactionHandlingAmountDue;

    private String safekeepingItem;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String otherItem;
    private String otherFee;
    private String otherAmountDue;

    private String totalAmountDueBeforeTax;

    private String taxFee;
    private String taxAmountDue;

    private String totalAmountDueAfterTax;

}
