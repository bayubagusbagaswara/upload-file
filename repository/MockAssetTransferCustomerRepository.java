package com.services.billingservice.repository;

import com.services.billingservice.model.MockAssetTransferCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockAssetTransferCustomerRepository extends JpaRepository<MockAssetTransferCustomer, Long> {

    List<MockAssetTransferCustomer> findByCustomerCodeAndSecurityCode(String customerCode, String securityCode);

}
