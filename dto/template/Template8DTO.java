package com.services.billingservice.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * untuk core type 10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template8DTO {

    private String safekeepingValueFrequency;
    private String safekeepingFee;
    private String safekeepingAmountDue;

    private String transactionHandlingValueFrequency;
    private String transactionHandlingFee;
    private String transactionHandlingAmountDue;

    private String proxyServiceValueFrequency;

    private String totalAmountDue;

}
