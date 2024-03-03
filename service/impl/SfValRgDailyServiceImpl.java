package com.services.billingservice.service.impl;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.repository.SfValRgDailyRepository;
import com.services.billingservice.service.SfValRgDailyService;
import com.services.billingservice.utils.CsvDataMapper;
import com.services.billingservice.utils.CsvReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class SfValRgDailyServiceImpl implements SfValRgDailyService {

    private final SfValRgDailyRepository sfValRgDailyRepository;

    public SfValRgDailyServiceImpl(SfValRgDailyRepository sfValRgDailyRepository) {
        this.sfValRgDailyRepository = sfValRgDailyRepository;
    }

    @Override
    public String readFileAndInsertToDB(String filePath) {
        log.info("Start read and insert SfVal RG Daily to the database : {}", filePath);
        try {
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<BillingSfvalRgDaily> rgDailyList = CsvDataMapper.mapCsvSfValRgDaily(rows);

            sfValRgDailyRepository.saveAll(rgDailyList);

            return "[SfVal RG Daily] CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "[SfVal RG Daily] Failed to process CSV File : " + e.getMessage();
        }
    }

    @Override
    public List<BillingSfvalRgDaily> getAll() {
        log.info("Get all SfVal RG Daily");
        return sfValRgDailyRepository.findAll();
    }

    @Override
    public List<BillingSfvalRgDaily> getAllByAid(String aid) {
        log.info("Start get all SfVal RG Daily with Aid : {}", aid);
        return sfValRgDailyRepository.findAllByAid(aid);
    }

    @Override
    public List<BillingSfvalRgDaily> getAllByAidAndDate(String aid, LocalDate monthYear) {
        log.info("Start get all SfVal RG Daily with Aid : {}, and Date : {}", aid, monthYear);
        return sfValRgDailyRepository.findAllByAidAndDate(aid, monthYear);
    }

    @Override
    public List<BillingSfvalRgDaily> getAllByAidAndMonthAndYear(String aid, String month, Integer year) {
        log.info("Start get all SfVal RG Daily with AID : {}, Month : {}, and Year : {}", aid, month, year);
        return sfValRgDailyRepository.findAllByAidAndMonthAndYear(aid, month, year);
    }

    @Override
    public List<BillingSfvalRgDaily> getAllByAidAndSecurityName(String aid, String securityName) {
        log.info("Start get all SfVal RG Daily with AID : {} and Security Name : {}", aid, securityName);
        return sfValRgDailyRepository.findAllByAidAndSecurityName(aid, securityName);
    }

    @Override
    public String deleteAll() {
        try {
            sfValRgDailyRepository.deleteAll();
            return "Successfully deleted all SfVal RG Daily data";
        } catch (Exception e) {
            log.error("Error when delete all SfVal RG Daily : " + e.getMessage(), e);
            throw new RuntimeException("Error when delete all SfVal RG Daily");
        }
    }

}
