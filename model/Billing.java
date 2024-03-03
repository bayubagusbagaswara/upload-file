package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bill_billing")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "period")
    private LocalDate period;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private String year;

    @Column(name = "accrual_custodial_fee")
    private BigDecimal accrualCustodialFee; //

    @Column(name = "value_frequency_S4")
    private Integer valueFrequencyS4; // BI-SSSS

    @Column(name = "amount_s4")
    private BigDecimal amountS4;

    @Column(name = "total_nominal_before_tax")
    private BigDecimal totalNominalBeforeTax;

    @Column(name = "amount_tax")
    private BigDecimal amountTax;

    @Column(name = "value_frequency_ksei")
    private Integer valueFrequencyKSEI; // CBEST

    @Column(name = "amount_ksei")
    private BigDecimal amountKSEI;

    @Column(name = "total_nominal_after_tax")
    private BigDecimal totalNominalAfterTax;


    // tambahkan field status dan description generate billing

}
