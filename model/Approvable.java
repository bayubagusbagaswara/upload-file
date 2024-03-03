package com.services.billingservice.model;

import com.services.billingservice.enums.ApprovalStatus;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Date;


@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Approvable extends BeanEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name = "inputer_id")
    private String inputerId;

    @Column(name = "input_date")
    private Date inputDate;

    @Column(name = "approver_id")
    private String approverId;

    @Column(name = "approve_date")
    private Date approveDate;

}
