package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * for repesentration sktrans data
 */
@Entity
@Table(name = "bill_sktran")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingSKTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trade_id")
    private String tradeId;

    @Column(name = "portfolio_code")
    private String portfolioCode;

    @Column(name = "security_type")
    private String securityType;

    @Column(name = "security_short_name")
    private String securityShortName; // ganti menjadi securityShortName

    @Column(name = "security_name")
    private String securityName;

    @Column(name = "type")
    private String type;

    @Column(name = "trade_date")
    private LocalDate tradeDate;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "delete_status")
    private String deleteStatus;

    @Column(name = "system")
    private String system;

    @Column(name = "sid")
    private String sid;

    @Column(name = "remark")
    private String remark;

}
