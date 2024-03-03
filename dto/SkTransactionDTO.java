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
public class SkTransactionDTO {

    private Long id;

    private String portfolioCode;

    private LocalDate tradeDate;

    private LocalDate settlementDate;

    private BigDecimal amount;

    private String settlementSystem;

}
