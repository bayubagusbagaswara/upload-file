package com.services.billingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeReportDTO {

    private String portfolioCode;

    private String accrualCustodialFee;

    private String date; // Nov 2023

}
