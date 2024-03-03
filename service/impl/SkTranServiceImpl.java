package com.services.billingservice.service.impl;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.repository.BillingSKTransactionRepository;
import com.services.billingservice.service.SkTranService;
import com.services.billingservice.utils.CsvDataMapper;
import com.services.billingservice.utils.CsvReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class SkTranServiceImpl implements SkTranService {

    private final BillingSKTransactionRepository skTransactionRepository;

    public SkTranServiceImpl(BillingSKTransactionRepository skTransactionRepository) {
        this.skTransactionRepository = skTransactionRepository;
    }

    @Override
    public String readFileAndInsertToDB(String filePath) throws IOException, CsvException {
        log.info("Start read and insert SkTransaction to the database : {}", filePath);
        try {
            List<String[]> rows = CsvReaderUtil.readCsvFile(filePath);

            List<BillingSKTransaction> skTransactionList = CsvDataMapper.mapCsvSkTransaction(rows);

            skTransactionRepository.saveAll(skTransactionList);

            return "[SK Transaction] CSV data processed and saved successfully";
        } catch (IOException | CsvException e) {
            return "[SK Transaction] Failed to process CSV file : " + e.getMessage();
        }
    }

    @Override
    public List<BillingSKTransaction> getAll() {
        log.info("Start get all SK TRAN");
        return skTransactionRepository.findAll();
    }

    @Override
    public List<BillingSKTransaction> getAllByPortfolioCode(String portfolioCode) {
        log.info("Start get all SK TRAN with portfolio code : {}", portfolioCode);
        return skTransactionRepository.findAllByPortfolioCode(portfolioCode);
    }

    @Override
    public List<BillingSKTransaction> getAllByPortfolioCodeAndSettlementDate(String portfolioCode, LocalDate settlementDate) {
        log.info("Start get all SK TRAN by portfolio code : {}, and settlement date : {}", portfolioCode, settlementDate);
        return skTransactionRepository.findAllByPortfolioCodeAndSettlementDate(
                portfolioCode,
                settlementDate
        );
    }

    @Override
    public List<BillingSKTransaction> getAllByAidAndMonthAndYear(String aid, String month, Integer year) {
        log.info("Start get all SK TRAN by AID : {}, Month : {}, and Year : {}", aid, month, year);
        return skTransactionRepository.findAllByPortfolioCodeAndMonthAndYear(
                aid,
                month,
                year
        );
    }

    @Override
    public List<BillingSKTransaction> getAllByPortfolioCodeAndSystem(String portfolioCode, String system) {
        log.info("Start get all SK TRAN with portfolio code : {}, and system : {}", portfolioCode, system);
        return skTransactionRepository.findAllByPortfolioCodeAndSystem(portfolioCode, system);
    }

    @Override
    public String deleteAll() {
        try {
            skTransactionRepository.deleteAll();
            return "Successfully deleted all SK TRAN";
        } catch (Exception e) {
            log.error("Error when delete all SK TRAN : " + e.getMessage());
            throw new RuntimeException("Error when delete all SK TRAN");
        }
    }

}
