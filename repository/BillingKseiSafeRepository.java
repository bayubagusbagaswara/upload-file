package com.services.billingservice.repository;

import com.services.billingservice.model.BillingKseiSafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillingKseiSafeRepository extends JpaRepository<BillingKseiSafe, Long> {

    @Query(value = "SELECT * FROM bill_ksei_safe WHERE ksei_safe_code = :kseiSafeCode", nativeQuery = true)
    Optional<BillingKseiSafe> findByKseiSafeCode(@Param("kseiSafeCode") String kseiSafeCode);

    Optional<BillingKseiSafe> findByKseiSafeCodeAndMonthAndYear(
            String kseiSafeCode,
            String month,
            int year
    );

    // cari 3 data terakhir

}
