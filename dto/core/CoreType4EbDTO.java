package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType4EbDTO {

    private String aid;

    private String billingTemplate;

    private String kseiSafekeepingAmountDue;

    private String kseiTransactionValueFrequency;

    private String kseiTransactionFee;

    private String kseiTransactionAmountDue;

    private String totalAmountDue;
}
