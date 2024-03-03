package com.services.billingservice.repository;

import com.services.billingservice.model.BillingSfvalRgMonthly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SfValRgMonthlyRepository extends JpaRepository<BillingSfvalRgMonthly, Long> {

    List<BillingSfvalRgMonthly> findAllByAid(String aid);

    Optional<BillingSfvalRgMonthly> findByAidAndSecurityName(String aid, String securityName);

    List<BillingSfvalRgMonthly> findAllByAidAndMonthAndYear(
            String aid,
            String month,
            int year
    );

}
