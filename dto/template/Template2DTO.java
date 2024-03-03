package com.services.billingservice.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template2DTO {

    // Safekeeping Fee
    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    // VAT
    private String vatFee;
    private String vatAmountDue;

    // KSEI Fee - Transaction
    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;

    // KSEI Fee - Safekeeping
    private String kseiSafekeepingAmountDue;

    // Total
    private String totalAmountDue;

}
