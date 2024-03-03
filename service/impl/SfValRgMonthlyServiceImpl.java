package com.services.billingservice.service.impl;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingSfvalRgMonthly;
import com.services.billingservice.repository.SfValRgMonthlyRepository;
import com.services.billingservice.service.SfValRgMonthlyService;
import com.services.billingservice.utils.CsvDataMapper;
import com.services.billingservice.utils.CsvReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SfValRgMonthlyServiceImpl implements SfValRgMonthlyService {

    private final SfValRgMonthlyRepository sfValRgMonthlyRepository;

    public SfValRgMonthlyServiceImpl(SfValRgMonthlyRepository sfValRgMonthlyRepository) {
        this.sfValRgMonthlyRepository = sfValRgMonthlyRepository;
    }

    @Override
    public String readFileAndInsertToDB(String filePath) {
        log.info("Start read and insert SfVal RG Monthly to the database : {}", filePath);

        try {
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<BillingSfvalRgMonthly> rgMonthlyList = CsvDataMapper.mapCsvSfValRgMonthly(rows);

            sfValRgMonthlyRepository.saveAll(rgMonthlyList);

            return "[SfVal RG Monthly] CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "[SfVal RG Monthly] Failed to process CSV File : " + e.getMessage();
        }
    }

    @Override
    public List<BillingSfvalRgMonthly> getAll() {
        log.info("Get all SfVal RG Monthly");
        return sfValRgMonthlyRepository.findAll();
    }

    @Override
    public List<BillingSfvalRgMonthly> getAllByAid(String aid) {
        log.info("Start get all SfVal RG Monthly with Aid : {}", aid);
        return sfValRgMonthlyRepository.findAllByAid(aid);
    }

    @Override
    public BillingSfvalRgMonthly getAllByAidAndSecurityName(String aid, String securityName) {
        log.info("Get SfVal RG Monthly by Aid : {} and Security Name : {}", aid, securityName);
        return sfValRgMonthlyRepository.findByAidAndSecurityName(aid, securityName)
                .orElseThrow(() -> new DataNotFoundException("SfVal RG Monthly not found with Aid : " + aid + " and Security Name : " + securityName));
    }

    @Override
    public List<BillingSfvalRgMonthly> getAllByAidAndMonthAndYear(String aid, String month, int year) {
        return sfValRgMonthlyRepository.findAllByAidAndMonthAndYear(aid, month, year);
    }
}
