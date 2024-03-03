package com.services.billingservice.repository.compliance;

import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.model.compliance.Reksadana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface ReksadanaRepository extends JpaRepository<Reksadana,Long> {
//    Reksadana findByCode(String code);

    @Query(value="select * from comp_reksadana WHERE code = :code and isDelete = 'false'", nativeQuery = true)
    Reksadana findByCode(
            @Param("code") String code);

    Reksadana findByCodeAndDelete(String code, boolean delete);

    @Query("SELECT u FROM Reksadana u WHERE u.approvalStatus = :approvalStatus and u.code like %:code% and u.delete = false ")
    List<Reksadana> searchByCodeLike(
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("code") String code);

    @Query("SELECT u FROM Reksadana u WHERE u.approvalStatus = :approvalStatus and u.externalCode like %:externalCode% and u.delete = false")
    List<Reksadana> searchByExternalCodeLike(
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("externalCode") String externalCode);

    @Query("SELECT u FROM Reksadana u WHERE u.approvalStatus = :approvalStatus and u.name like %:name% and u.delete = false")
    List<Reksadana> searchByNameLike(
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("name") String name);

    List<Reksadana> findAllByDeleteAndApprovalStatus(boolean isDelete, ApprovalStatus approvalStatus);

    @Query("SELECT u FROM Reksadana u WHERE  u.approvalStatus = 'Pending' and u.delete = false")
    List<Reksadana> searchPendingData();

    @Transactional
    @Modifying
    @Query(value="UPDATE comp_reksadana SET approval_status = :approvalStatus, approve_date = :approveDate, approver_id = :approverId " +
            "WHERE code = :code", nativeQuery = true)
    void approveOrRejectReksadana(@Param("approvalStatus") String approvalStatus,
                                     @Param("approveDate") Date approveDate,
                                     @Param("approverId") String approverId,
                                     @Param("code") String reksadanaCode);
}
