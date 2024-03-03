package com.services.billingservice.repository;

import com.services.billingservice.model.MockKycCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockKycCustomerRepository extends JpaRepository<MockKycCustomer, Long> {

    List<MockKycCustomer> findByAid(String aid);

    List<MockKycCustomer> findAllByBillingCategoryAndBillingType(String billingCategory, String billingType);

}
