package com.services.billingservice.service;

import com.services.billingservice.model.BillingKseiSafe;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface KseiSafeService {

    String readAndInsertToDB(String filePath);

    List<BillingKseiSafe> getAll();

    BillingKseiSafe getByKseiSafeCode(String kseiSafeCode);

    // format feeAccount = BDMN2MUFG00146
    BigDecimal calculateAmountFeeByKseiSafeCodeAndDate(String kseiSafeCode, LocalDate date);

    // calculate amount fee 3 bulan terakhir
//    Calculate the numbers for the last 3 months

    BigDecimal calculateAmountFeeLatest3Month(String kseiSafeCode, LocalDate date);

    BigDecimal calculateAmountFeeByKseiSafeCodeAndMonthAndYear(
            String kseiSafeCode,
            String month,
            int year
    );

    String deleteAll();

}
