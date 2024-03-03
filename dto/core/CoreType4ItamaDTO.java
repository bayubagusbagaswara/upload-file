package com.services.billingservice.dto.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreType4ItamaDTO {

    private String aid;

    private String billingTemplate;

    private String safekeepingValueFrequency;

    private String safekeepingFee;

    private String safekeepingAmountDue;

    private String vatFee;

    private String vatAmountDue;

    private String totalAmountDue;

}
