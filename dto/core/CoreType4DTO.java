package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType4DTO {

    private String aid;
    private String billingTemplate;
    private String period; // Nov 2023

    // EB
    private String kseiSafekeepingAmountDue;
    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;
    private String totalAmountDueEB;

    // Itama
    private String safekeepingFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;
    private String vatFee;
    private String vatAmountDue;
    private String totalAmountDueItama;

}
