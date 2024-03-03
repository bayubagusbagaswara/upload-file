package com.services.billingservice.repository;

import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.model.BillingDataChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingDataChangeRepository extends JpaRepository<BillingDataChange, Long> {

    List<BillingDataChange> findByApprovalStatus(ApprovalStatus ApprovalStatus);

    List<BillingDataChange> findByEntityClassName(String entityClassName);

    @Query(value = "SELECT * FROM bill_data_change WHERE approval_status = 'Pending'", nativeQuery = true)
    List<BillingDataChange> searchAllByApprovalStatusPending();

    @Query(value = "SELECT * FROM bill_data_change WHERE id = :idData AND approval_status = 'Pending'", nativeQuery = true)
    BillingDataChange searchDataForApproveOrReject(@Param("idData") Long id);

}
