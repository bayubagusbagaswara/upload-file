package com.services.billingservice.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sama dengan type 3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template7DTO {

    private String safekeepingValueFrequency;

    private String safekeepingFee;

    private String safekeepingAmountDue;

    private String safekeepingCreditTo;

}
