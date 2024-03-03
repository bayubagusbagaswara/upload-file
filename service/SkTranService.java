package com.services.billingservice.service;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.model.BillingSKTransaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface SkTranService {

    String readFileAndInsertToDB(String filePath) throws IOException, CsvException;

    List<BillingSKTransaction> getAll();

    List<BillingSKTransaction> getAllByPortfolioCode(String portfolioCode);

    List<BillingSKTransaction> getAllByPortfolioCodeAndSettlementDate(String portfolioCode, LocalDate settlementDate);

    List<BillingSKTransaction> getAllByAidAndMonthAndYear(String aid, String month, Integer year);

    List<BillingSKTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system);

    String deleteAll();

}
