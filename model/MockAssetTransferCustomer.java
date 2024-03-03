package com.services.billingservice.model;

import com.services.billingservice.enums.AssetTransferCustomerStatus;
import com.services.billingservice.enums.AssetTransferCustomerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mock_asset_transfer_customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockAssetTransferCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "security_code")
    private String securityCode;

    // status ENABLE artinya customer code dan security code ini sedang berstatus nasabah transfer asset
    // status DISABLE artinya customer code dan security code ini bukan berstatus nasabah transfer asset
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssetTransferCustomerStatus status = AssetTransferCustomerStatus.INACTIVE;

    // NO_TRANSFER, FULL or PARTIAL
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AssetTransferCustomerType type = AssetTransferCustomerType.NO_TRANSFER;

    // amount adalah holding yang di-transferkan
    // jadi nilai sekarang adalah nilai FACE_VALUE_RG_DAILY dikurangin AMOUNT ini
    // tapi di cek dulu apakah aid dan security code itu berstatus active dan type nya apa
    @Column(name = "amount")
    private BigDecimal amount; // 15000000.00

    @Column(name = "effective_date")
    private LocalDate effectiveDate; // 2024-11-01

}
