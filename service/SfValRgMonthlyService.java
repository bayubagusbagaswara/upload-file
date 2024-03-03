package com.services.billingservice.service;

import com.services.billingservice.model.BillingSfvalRgMonthly;

import java.util.List;

public interface SfValRgMonthlyService {

    String readFileAndInsertToDB(String filePath);

    List<BillingSfvalRgMonthly> getAll();

    List<BillingSfvalRgMonthly> getAllByAid(String aid); // 13KONI

    BillingSfvalRgMonthly getAllByAidAndSecurityName(String aid, String securityName);

    // get by aid and month and year
    // Response nya:
    // 12MUFG = security name MUFG
    // 12MUFG = security name GUDH
    List<BillingSfvalRgMonthly> getAllByAidAndMonthAndYear(
            String aid,
            String month,
            int year
    );

}
