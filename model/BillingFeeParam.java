package com.services.billingservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "billing_fee_param")
public class BillingFeeParam extends Approvable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fee_code")
    String feeCode;

    @Column(name = "fee_name")
    String feeName;

    @Column(name = "fee_value")
    double value;

    @Column(name = "fee_description")
    String description;
}
