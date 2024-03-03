package com.services.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDTO {

    private String aid;

    private String kseiSafeCode;

    private String billingCategory;

    private String billingType;

    private String billingTemplate;

    private BigDecimal minimumFee;
}
