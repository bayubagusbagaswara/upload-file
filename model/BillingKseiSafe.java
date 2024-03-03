package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bill_ksei_safe")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKseiSafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "month")
    private String month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "fee_description")
    private String feeDescription;

    @Column(name = "ksei_safe_code")
    private String kseiSafeCode;

    @Column(name = "amount_fee")
    private BigDecimal amountFee;

}
