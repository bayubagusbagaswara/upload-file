package com.services.billingservice.repository;

import com.services.billingservice.model.BillingFeeParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingFeeParamRepository extends JpaRepository<BillingFeeParam, Long> {

    @Query(value = "SELECT * FROM billing_fee_param where fee_code = :code", nativeQuery = true)
    Optional<BillingFeeParam> findByCode(@Param("code")String code);

    @Query(value = "SELECT * FROM billing_fee_param", nativeQuery = true)
    List<BillingFeeParam> findAll();

    @Query(value = "SELECT * FROM billing_fee_param", nativeQuery = true)
    List<BillingFeeParam>findByCodeList(@Param("code")String code);

}
