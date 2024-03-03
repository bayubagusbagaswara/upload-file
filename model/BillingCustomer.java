package com.services.billingservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "billing_customer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCustomer extends Approvable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "category")
    private String category;

    @Column(name = "type")
    private String type;

    @Column(name = "nama_mi")
    private String namaMI;

    @Column(name = "alamat_mi")
    private String alamatMI;

    @Column(name = "debit_transfer")
    private String debitTransfer;

    @Column(name = "account")
    private String account;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "safekeeping_fee")
    private String safekeepingFee;

    @Column(name = "vat")
    private double vat;

    @Column(name = "ksei_transaction")
    private double kseiTransaction;

    @Column(name = "bis4_transaction")
    private double bis4Transaction;

    @Column(name = "transaction_handling")
    private double transactionHandling;

    @Column(name = "proxy_services")
    private double proxyServices;

    @Column(name = "securities_transaction")
    private double securitiesTransaction;

    @Column(name = "transaction_fee")
    private double transactionFee;

    @Column(name = "gl_account_hasil")
    private String glAccountHasil;

    @Column(name = "npwp")
    private String npwp;

    @Column(name = "nama_npwp")
    private String nameNPWP;

    @Column(name = "costCenter")
    private String costCenter;

    @Column(name = "safekeepingKsei")
    private double safekeepingKsei;

}
