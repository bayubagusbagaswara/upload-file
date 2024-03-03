package com.services.billingservice.repository;

import com.services.billingservice.model.Billing;
import com.services.billingservice.model.BillingSKTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillingSKTransactionRepository extends JpaRepository<BillingSKTransaction, Long> {

    @Query(value = "SELECT * FROM bill_sktran WHERE portfolio_code = :portfolioCode", nativeQuery = true)
    List<BillingSKTransaction> findAllByPortfolioCode(@Param("portfolioCode") String portfolioCode);

    @Query(value = "SELECT * FROM bill_sktran WHERE portfolio_code = :portfolioCode AND system = :system", nativeQuery = true)
    List<BillingSKTransaction> findAllByPortfolioCodeAndSystem(
            @Param("portfolioCode") String portfolioCode,
            @Param("system") String system
    );

    List<BillingSKTransaction> findAllByPortfolioCodeAndSettlementDate(
            String portfolioCode,
            LocalDate settlementDate
    );

    List<BillingSKTransaction> findAllByPortfolioCodeAndMonthAndYear(
            String portfolioCode,
            String month,
            Integer year
    );

}
