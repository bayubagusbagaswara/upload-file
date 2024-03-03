package com.services.billingservice.service;

import com.services.billingservice.model.BillingSfvalRgDaily;

import java.time.LocalDate;
import java.util.List;

public interface SfValRgDailyService {

    String readFileAndInsertToDB(String filePath);

    List<BillingSfvalRgDaily> getAll();

    List<BillingSfvalRgDaily> getAllByAid(String aid);

    List<BillingSfvalRgDaily> getAllByAidAndDate(String aid, LocalDate date);

    List<BillingSfvalRgDaily> getAllByAidAndMonthAndYear(String aid, String month, Integer year);

    List<BillingSfvalRgDaily> getAllByAidAndSecurityName(String aid, String securityName);

    String deleteAll();
}
