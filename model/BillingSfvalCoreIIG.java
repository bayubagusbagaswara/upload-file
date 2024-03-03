package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_sfval_core_iig")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingSfvalCoreIIG {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code_group", columnDefinition = "varchar(255) default 'IIG'")
    private String customerCodeGroup;

    @Column(name = "customer_code")
    private String customerCode; // aid

    @Column(name = "customer_name")
    private String customerName; // Alam Manunggal (ALMAN), Indo Infrastruktur (INFRAS), Mandala Kapital (MANKAP)

    @Column(name = "date")
    private Integer date; // 1-31

    @Column(name = "total_holding")
    private BigDecimal totalHolding;

    @Column(name = "price_trub")
    private Integer priceTRUB;

    @Column(name = "total_market_value")
    private BigDecimal totalMarketValue;

    @Column(name = "safekeeping_fee")
    private BigDecimal safekeepingFee;

}
