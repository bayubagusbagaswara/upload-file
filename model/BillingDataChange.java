package com.services.billingservice.model;

import com.services.billingservice.enums.ChangeAction;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "bill_data_change")
public class BillingDataChange extends Approvable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private ChangeAction action;

    @Column(name = "entity_class_name")
    private String entityClassName;

    @Column(name = "entity_id" )
    private String entityId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "data_before")
    private String dataBefore;

    @Lob
    @Column(name = "data_after")
    private String dataChange;

}
