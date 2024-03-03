package com.services.billingservice.dto.fund;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeReportRequest {

    @JsonProperty(value = "ID")
    private Integer number;

    @JsonProperty(value = "AID")
    private String portfolioCode;

    @JsonProperty(value = "Cus Fee (Gross)")
    private BigDecimal customerFee; // accrual custodial fee

}
