package com.services.billingservice.repository;

import com.services.billingservice.model.BillingSfvalCoreIIG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SfValCoreIIGRepository extends JpaRepository<BillingSfvalCoreIIG, Long> {

    // SELECT * FROM Data d WHERE d.aid = :aid LIMIT :limit

    List<BillingSfvalCoreIIG> findAllByCustomerCodeOrderByDateAsc(String customerCode);

}
