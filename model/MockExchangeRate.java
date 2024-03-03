package com.services.billingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "mock_exchange_rate")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "currency")
    private String currency;

    @Column(name = "value")
    private BigDecimal value;

}
