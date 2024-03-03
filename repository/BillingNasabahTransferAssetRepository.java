package com.services.billingservice.repository;

import com.services.billingservice.model.BillingNasabahTransferAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingNasabahTransferAssetRepository extends JpaRepository<BillingNasabahTransferAsset, Long> {

    @Query(value = "SELECT * FROM bill_nasabah_transfer_asset WHERE security_code = :securityCode AND is_deleted = 0", nativeQuery = true)
    Optional<BillingNasabahTransferAsset> findBySecurityCodeAndIsDeletedFalse(@Param("securityCode") String securityCode);

    @Query(value = "SELECT * FROM bill_nasabah_transfer_asset WHERE is_deleted = 0", nativeQuery = true)
    List<BillingNasabahTransferAsset> findAllAndIsDeletedFalse();

    @Query(value = "SELECT * FROM bill_nasabah_transfer_asset WHERE id = :id AND is_deleted = 0", nativeQuery = true)
    Optional<BillingNasabahTransferAsset> findByIdAndIsDeletedFalse(@Param("id") Long id);
}
