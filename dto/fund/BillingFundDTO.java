package com.services.billingservice.dto.fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingFundDTO {

    private String portfolioCode;

    private String period;

    private String amountDueAccrualCustody;

    private String valueFrequencyS4;

    private String s4Fee; // 23.000

    private String amountDueS4;

    private String totalNominalBeforeTax;

    private String taxFee; // 0.11 (11%)

    private String amountDueTax;

    private String valueFrequencyKSEI;

    private String kseiFee; // 22.200

    private String amountDueKSEI;

    private String totalAmountDue;
}
