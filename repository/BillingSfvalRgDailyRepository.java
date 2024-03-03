package com.services.billingservice.repository;

import com.services.billingservice.model.BillingSfvalRgDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillingSfvalRgDailyRepository extends JpaRepository<BillingSfvalRgDaily, Long> {

    @Query(value = "SELECT * FROM bill_sfval_rg_daily WHERE aid = :aid", nativeQuery = true)
    List<BillingSfvalRgDaily> findAllByAid(@Param("aid") String aid);

    @Query(value = "SELECT * FROM bill_sfval_rg_daily WHERE aid = :aid AND security_name = :securityName", nativeQuery = true)
    List<BillingSfvalRgDaily> findAllByAidAndSecurityName(@Param("aid") String aid, @Param("securityName") String securityName);

    @Query(value = "SELECT * FROM bill_sfval_rg_daily WHERE aid = :aid AND date = :latestDate", nativeQuery = true)
    List<BillingSfvalRgDaily> findAllByAidAndDate(@Param("aid") String aid, @Param("latestDate") LocalDate latestDate);

}
