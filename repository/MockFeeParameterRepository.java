package com.services.billingservice.repository;

import com.services.billingservice.model.MockFeeParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MockFeeParameterRepository extends JpaRepository<MockFeeParameter, Long> {

    Optional<MockFeeParameter> findByName(String name);

//    @Query(value = "SELECT f.value FROM MockFeeParameter f " +
//            "WHERE f.name IN :names", nativeQuery = true)
//    List<BigDecimal> findValueByNameList(
//            @Param("names") List<String> names
//            );

    @Query(value = "SELECT f FROM MockFeeParameter f " +
            "WHERE f.name IN :names", nativeQuery = true)
    List<MockFeeParameter> findMockFeeParameterByNameList(
            @Param("names") List<String> names
    );

}
