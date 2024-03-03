package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.exception.ExcelProcessingException;
import com.services.billingservice.model.BillingKseiSafe;
import com.services.billingservice.repository.BillingKseiSafeRepository;
import com.services.billingservice.service.KseiSafeService;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.utils.ConvertBigDecimalUtil;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class KseiSafeServiceImpl implements KseiSafeService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private final BillingKseiSafeRepository kseiSafeRepository;
    private final MockFeeParameterService feeParameterService;

    @Override
    public String readAndInsertToDB(String filePath) {
        log.info("File Path : {}", filePath);

        List<BillingKseiSafe> kseiSafeList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)))) {
            processSheets(workbook, kseiSafeList);
            kseiSafeRepository.saveAll(kseiSafeList);
            return "Excel data processed and saved successfully";
        } catch (IOException e) {
            log.error("Error reading the Excel file: {}", e.getMessage());
            return "Failed to process Excel file: " + e.getMessage();
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

    @Override
    public List<BillingKseiSafe> getAll() {
        return kseiSafeRepository.findAll();
    }

    @Override
    public BillingKseiSafe getByKseiSafeCode(String kseiSafeCode) {
        return kseiSafeRepository.findByKseiSafeCode(kseiSafeCode)
                .orElseThrow(() -> new DataNotFoundException("Data not found with ksei safe code : " + kseiSafeCode));
    }

    @Override
    public BigDecimal calculateAmountFeeByKseiSafeCodeAndDate(String kseiSafeCode, LocalDate date) {
        // format date to LocalDate
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
//        LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
//        log.info("Date : {}", localDate);

        // di table nanti ada beberapa data untuk kseiSafeCode tapi date nya berbeda-beda
        // kseiSafeCode BDMN2MUFG00146, date = 2023-10-31
        // kseiSafeCode BDMN2MUFG00146, date = 2023-11-30
        // kseiSafeCode BDMN2MUFG00146, date = 2023-12-31

        // jadi kita harus cari berdasarkan portfolio code dan date

//        List<DataIndicated> findAllByDocumentAndDataAfter(String numberDocument, LocalDateTime localDateTime);

//        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(1);


        // TODO: get Ksei Safe by ksei_safe_code and date
        // TODO: berarti kita tambahkan file aja Month dan Year di table KSEI SAFE
        // TODO: Month dan Year diambil dari kolom created date
        // TODO: Created Date menandakan bahwa kapan data KSEI Safe ini dibuat


        String month = "November";
        int year = 2023;
        BillingKseiSafe kseiSafe = kseiSafeRepository.findByKseiSafeCodeAndMonthAndYear(
                kseiSafeCode, month, year)
                .orElseThrow(() -> new DataNotFoundException("Data Not Found with Ksei Safe Code : " + kseiSafeCode));

        // get amount fee
        BigDecimal amountFee = kseiSafe.getAmountFee();

        /** TODO: GET PPN value from table parameterize */
        BigDecimal ppnFee = new BigDecimal(0.11);

        BigDecimal valueAfterPPN = amountFee
                .multiply(ppnFee)
                .setScale(0, RoundingMode.HALF_UP);

        return amountFee
                .add(valueAfterPPN)
                .setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateAmountFeeByKseiSafeCodeAndMonthAndYear(String kseiSafeCode, String month, int year) {
        // TODO: Retrieve Fee Parameter VAT
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameterNameConstant.VAT);
        log.info("[Ksei Safe Service] VAT Fee : {}", vatFee);

        BillingKseiSafe kseiSafe = kseiSafeRepository.findByKseiSafeCodeAndMonthAndYear(
                kseiSafeCode, month, year)
                .orElseThrow(() -> new DataNotFoundException("Data Not Found with Ksei Safe Code : " + kseiSafeCode));

        // TODO: Amount Fee
        BigDecimal amountFee = kseiSafe.getAmountFee();
        log.info("[Ksei Safe Service] Amount Fee with Ksei Safe Code : {}, is : {}",
                kseiSafe.getKseiSafeCode(), amountFee);

        // TODO: Calculate Amount Fee * VAT Fee
        BigDecimal valueAfterPPN = amountFee
                .multiply(vatFee)
                .setScale(0, RoundingMode.HALF_UP);

        // TODO: Calculate Value After PPN + Amount Fee
        BigDecimal resultSafekeepingKsei = amountFee
                .add(valueAfterPPN)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("[Ksei Safe Service] Result Safekeeping Ksei : {}", resultSafekeepingKsei);

        return resultSafekeepingKsei;
    }

    @Override
    public BigDecimal calculateAmountFeeLatest3Month(String kseiSafeCode, LocalDate date) {
        // dapatkan data 3 bulan terakhir
        LocalDate startDate = LocalDate.of(2023, 10, 30);
        LocalDate endDate = LocalDate.of(2023, 8, 30);

        // panggil repository dengan parameter kseiSafeCode, startDate, endDate
//        List<BillingKseiSafe> kseiSafeList = kseiSafeRepository.findAllByKseiSafeCodeIsAfter(kseiSafeCode, startDate, endDate);

//        for (BillingKseiSafe kseiSafe : kseiSafeList) {
//            log.info("To Date : {}", kseiSafe.getToDate());
//            log.info("KSEI Safe Code : {}", kseiSafe.getKseiSafeCode());
//            log.info("Amount Fee : {}", kseiSafe.getAmountFee());
//        }

        // ambil amount fee dari masing-masing data tersebut
//        BigDecimal totalAmountFee = kseiSafeList.stream()
//                .map(BillingKseiSafe::getAmountFee)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        log.info("Total Amount Fee KSEI Safe Code : {} is {}", kseiSafeCode, totalAmountFee);

        // tambahkan 3 data tersebut = total

        return null;
    }

    @Override
    public String deleteAll() {
        try {
            kseiSafeRepository.deleteAll();
            return "Successfully deleted all Ksei Safe Fee";
        } catch (Exception e) {
            log.error("Error when delete all Ksei Safe Fee : " + e.getMessage());
            throw new RuntimeException("Error when delete all Ksei Safe Fee");
        }
    }

    private void processSheets(Workbook workbook, List<BillingKseiSafe> kseiSafeList) {
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();

        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            processRows(sheet, kseiSafeList);
        }
    }

    private void processRows(Sheet sheet, List<BillingKseiSafe> kseiSafeList) {
        Iterator<Row> rowIterator = sheet.rowIterator();

        // Skip the first row (header)
        if (rowIterator.hasNext()) {
            rowIterator.next(); // move to the next row
        }

        while (rowIterator.hasNext()) {
            try {
                Row row = rowIterator.next();
                BillingKseiSafe kseiSafekeepingFee = createEntityFromRow(row);
                kseiSafeList.add(kseiSafekeepingFee);
            } catch (Exception e) {
                log.error("Error processing a row: {}", e.getMessage());
                // You may choose to continue processing other rows or break the loop
                throw new ExcelProcessingException("Failed to process Excel file: " + e.getMessage(), e);
            }
        }
    }

    private static BillingKseiSafe createEntityFromRow(Row row) {
        BillingKseiSafe kseiSafekeepingFee = new BillingKseiSafe();
        Cell cell3 = row.getCell(2);

        LocalDate date = ConvertDateUtil.parseDateOrDefault(cell3.toString(), dateFormatter);
        Integer year = date != null ? date.getYear() : null;
        String monthName = date != null ? date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) : "";

        kseiSafekeepingFee.setCreatedDate(date);
        log.info("Created Date : {}", cell3.toString());

        kseiSafekeepingFee.setMonth(monthName);
        log.info("Month : {}", monthName);

        kseiSafekeepingFee.setYear(year);
        log.info("Year : {}", year);

        Cell cell14 = row.getCell(14);
        kseiSafekeepingFee.setFeeDescription(cell14.toString());
        log.info("Fee Description : {}", cell14.toString());

        String kseiSafeCode = checkContainsSafekeeping(cell14.toString());
        kseiSafekeepingFee.setKseiSafeCode(kseiSafeCode);
        log.info("KSEI Safe Code : {}", kseiSafeCode);

        Cell cell15 = row.getCell(15);
        BigDecimal amountFee = ConvertBigDecimalUtil.parseBigDecimalOrDefault(cell15.toString());
        kseiSafekeepingFee.setAmountFee(amountFee);
        log.info("Amount Fee : {}", amountFee);

        return kseiSafekeepingFee;
    }


    private static String checkContainsSafekeeping(String inputString) {
        String result;
        if (containsKeyword(inputString)) {
            result = cleanedDescription(inputString);
        } else {
            result = "";
        }
        return result;
    }

    private static boolean containsKeyword(String input) {
        return input.contains("Safekeeping fee for account");
    }

    private static String cleanedDescription(String inputContainsSafekeeping) {
        log.info("Input contains safekeeping : {}", inputContainsSafekeeping);
        String cleanedDescription = inputContainsSafekeeping.replace("Safekeeping fee for account", "").trim();
        log.info("Cleaned Description : {}", cleanedDescription);
        return cleanedDescription;
    }

}
