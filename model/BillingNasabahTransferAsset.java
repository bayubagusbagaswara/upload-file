package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bill_nasabah_transfer_asset")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingNasabahTransferAsset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "security_code")
    private String securityCode;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "amount")
    private double amount; // 15000000.00

    @Column(name = "effective_date")
    private String effectiveDate; // Jan 2024

}
