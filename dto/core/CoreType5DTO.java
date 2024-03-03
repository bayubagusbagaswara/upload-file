package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * kemungkinan ada 2 template
 * kita cocokan field mana saja yg masuk ke dalam template mana saja
 * misal field safekeepingValueFrequency hanya untuk template 1 dsb
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType5DTO {

    private String monthName;

    private String year;

    private String billingTemplate;

    private String safekeepingValueFrequency;

    private String safekeepingFee;

    private String safekeepingAmountDue;

    private String kseiAmountDue;

    private String totalAmountDue;

}
