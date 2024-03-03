package com.services.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingSfvalRgDailyDTO {

    private Long id;

    private Integer batch;

    private LocalDate date;

    private String aid;

    private String securityName;

    private BigDecimal faceValue;

    private String marketPrice;

    private BigDecimal marketValue;

    private BigDecimal estimationSkFee;

}
