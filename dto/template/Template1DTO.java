package com.services.billingservice.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template1DTO {

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;

    private String bis4TransactionValueFrequency;
    private String bis4TransactionFee;
    private String bis4TransactionAmountDue;

    private String kseiSafekeepingAmountDue;

    private String totalAmountDue;

}
