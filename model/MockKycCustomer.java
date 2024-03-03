package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "mock_kyc_customer_1")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockKycCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aid")
    private String aid;

    @Column(name = "ksei_safe_code")
    private String kseiSafeCode;

    @Column(name = "minimum_fee")
    private BigDecimal minimumFee; // 500.000

    @Column(name = "customer_fee")
    private double customerFee; // 0.05, 0.02

    @Column(name = "journal")
    private String journal;

    @Column(name = "billing_category")
    private String billingCategory;

    @Column(name = "billing_type")
    private String billingType;

    @Column(name = "billing_template")
    private String billingTemplate;

}
