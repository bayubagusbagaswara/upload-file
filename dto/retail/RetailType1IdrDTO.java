package com.services.billingservice.dto.retail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailType1IdrDTO {

    private String currency; // IDR

    private BigDecimal safekeepingFR;

    private BigDecimal safekeepingSR;

    private BigDecimal safekeepingST;

    private BigDecimal safekeepingORI;

    private BigDecimal safekeepingSBR;

    private BigDecimal safekeepingPBS;

    private BigDecimal safekeepingFeeCorporateBonds;

}
