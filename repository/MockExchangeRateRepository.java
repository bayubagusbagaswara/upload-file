package com.services.billingservice.repository;

import com.services.billingservice.model.MockExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MockExchangeRateRepository extends JpaRepository<MockExchangeRate, Long> {

    // find by currency and date

    @Override
    Optional<MockExchangeRate> findById(Long aLong);

    // date = '2023-11-30'
    Optional<MockExchangeRate> findByCurrencyAndDate(String currency, LocalDate date);
}
