package com.services.billingservice.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template5DTO {

    private String kseiTransactionValueFrequency;
    private String kseiTransactionFee;
    private String kseiTransactionAmountDue;

    private String kseiSafekeepingAmountDue;

    private String totalAmountDue;

}
