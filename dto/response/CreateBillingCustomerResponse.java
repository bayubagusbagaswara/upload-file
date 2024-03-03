package com.services.billingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillingCustomerResponse {

    private Long id;

    private String customerCode;

    private String category;

    private String type;

    private String namaMI;

    private String alamatMI;

    private String debitTransfer;

    private String account;

    private String accountName;

    private String safekeepingFee;

    private double vat;

    private double kseiTransaction;

    private double bis4Transaction;

    private double transactionHandling;

    private double proxyServices;

    private double securitiesTransaction;

    private double transactionFee;

    private String glAccountHasil;

    private String npwp;

    private String nameNPWP;

    private String costCenter;

    private double safekeepingKsei;

}
