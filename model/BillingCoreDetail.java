package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bill_core_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCoreDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aid;

    private String securityName;

    private BigDecimal marketValue;

    private BigDecimal estimationSkFee;

}
