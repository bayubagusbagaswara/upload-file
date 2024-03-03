package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType6DTO {

    private String safekeepingValueFrequency;

    private String safekeepingFee;

    private String safekeepingAmountDue;

    private String bis4ValueFrequency;

    private String bis4Fee;

    private String bis4AmountDue;

    private String totalBeforeVat;

    private String vatFee;

    private String vatAmountDue;

    private String kseiTransactionValueFrequency;

    private String kseiTransactionFee;

    private String kseiTransactionAmountDue;

    private String kseiSafekeepingAmountDue;

    private String totalAmountDue;
}
