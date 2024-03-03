package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bill_sfval_rg_monthly")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingSfvalRgMonthly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch")
    private Integer batch;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "year")
    private Integer year;

    @Column(name = "month")
    private String month;

    @Column(name = "aid")
    private String aid;

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "face_value")
    private BigDecimal faceValue;

    @Column(name = "market_price")
    private String marketPrice;

    @Column(name = "market_value")
    private BigDecimal marketValue;

    @Column(name = "estimation_sk_fee")
    private BigDecimal estimationSkFee;
}
