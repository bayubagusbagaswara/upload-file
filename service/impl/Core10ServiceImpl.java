package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType10DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.model.BillingSfvalRgMonthly;
import com.services.billingservice.service.Core10Service;
import com.services.billingservice.service.SfValRgMonthlyService;
import com.services.billingservice.service.SkTranService;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Treasury Syariah, Portfolio Code = 00N0IC
 * langsung ambil data Market Price nya
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Core10ServiceImpl implements Core10Service {

    private final MockKycCustomerService kycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final SkTranService skTranService;
    private final SfValRgMonthlyService sfValRgMonthlyService;

    @Override
    public List<CoreType10DTO> calculate(String category, String type, String monthYear) {
        // TODO: Call service Kyc Customer by Category and Type
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Get value by name TRANSACTION_HANDLING from service Fee Parameter
        BigDecimal transactionHandlingFee = feeParameterService.getValueByName(FeeParameterNameConstant.TRANSACTION_HANDLING_IDR);

        // TODO: Initialization variable
        int transactionHandlingFrequency;
        BigDecimal transactionHandlingAmountDue;
        BigDecimal safekeepingValueFrequency;
        BigDecimal safekeepingAmountDue;
        BigDecimal totalAmountDue;
        List<CoreType10DTO> coreType10DTOList = new ArrayList<>();

        // TODO: Convert Month Year
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        // TODO: Process calculate Billing
        for (int i = 1; i <= kycCustomerDTOList.size(); i++) {

            // TODO: Data Kyc Customer
            MockKycCustomerDTO kycCustomerDTO = kycCustomerDTOList.get(i);
            String aid = kycCustomerDTO.getAid();
            String customerFee = kycCustomerDTO.getCustomerFee();

            // TODO: Get data SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Get data Sf Val RG Monthly
            List<BillingSfvalRgMonthly> sfValRgMonthlyList = sfValRgMonthlyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Transaction Handling Value Frequency
            transactionHandlingFrequency = getTransactionHandlingFrequency(aid, skTransactionList);

            // TODO: Transaction Handling Amount Due
            transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(
                    transactionHandlingFrequency, transactionHandlingFee);

            // TODO: Safekeeping Value Frequency
            safekeepingValueFrequency = getSafekeepingValueFrequency(aid, sfValRgMonthlyList);

            // TODO: Safekeeping Amount Due
            safekeepingAmountDue = calculateSafekeepingAmountDue(safekeepingValueFrequency, customerFee);

            // TODO: Total Amount Due
            totalAmountDue = calculateTotalAmountDue(transactionHandlingAmountDue, safekeepingAmountDue);

            CoreType10DTO coreType10DTO = CoreType10DTO.builder()
                    .transactionHandlingValueFrequency(String.valueOf(transactionHandlingFrequency))
                    .transactionHandlingFee(String.valueOf(transactionHandlingFee))
                    .transactionHandlingAmountDue(String.valueOf(transactionHandlingAmountDue))
                    .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                    .safekeepingFee(String.valueOf(customerFee))
                    .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                    .totalAmountDue(String.valueOf(totalAmountDue))
                    .build();

            coreType10DTOList.add(coreType10DTO);
        }

        return coreType10DTOList;
    }

    private static int getTransactionHandlingFrequency(String aid, List<BillingSKTransaction> skTransactionList) {
        int totalTransactionHandlingFrequency;

        if (0 == skTransactionList.size()) {
            totalTransactionHandlingFrequency = 0;
        } else {
            totalTransactionHandlingFrequency = skTransactionList.size();
        }

        log.info("[Core Type 10] Transaction Handling Frequency with Aid : {}, is : {}", aid, totalTransactionHandlingFrequency);
        return totalTransactionHandlingFrequency;
    }

    private static BigDecimal calculateTransactionHandlingAmountDue(int transactionHandlingFrequency, BigDecimal transactionHandlingFee) {
        return new BigDecimal(transactionHandlingFrequency)
                .multiply(transactionHandlingFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal getSafekeepingValueFrequency(String aid, List<BillingSfvalRgMonthly> sfValRgMonthlyList) {
        Optional<LocalDate> latestDateOptional = sfValRgMonthlyList.stream()
                .map(BillingSfvalRgMonthly::getDate)
                .max(Comparator.naturalOrder());

        log.info("Latest Date Optional : {}", latestDateOptional);
        List<BillingSfvalRgMonthly> latestEntries = latestDateOptional
                .map(date -> sfValRgMonthlyList.stream()
                        .filter(entry -> entry.getDate().equals(date))
                        .collect(Collectors.toList())
                )
                .orElseGet(LinkedList::new);


        BigDecimal safeValueFrequency;

        if (0 == latestEntries.size()) {
            safeValueFrequency = BigDecimal.ZERO;
        } else {
            safeValueFrequency = latestEntries.stream()
                    .map(BillingSfvalRgMonthly::getMarketValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);
        }

        log.info("[Core Type 10] Safekeeping Value Frequency with Aid : {}, is : {}", aid, safeValueFrequency);
        return safeValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(BigDecimal safekeepingValueFrequency, String customerFee) {
        BigDecimal safeKeepingAfterDivide100Percentage = new BigDecimal(customerFee)
                .divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

        return safekeepingValueFrequency
                .multiply(safeKeepingAfterDivide100Percentage)
                .divide(new BigDecimal(12), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue) {
        return transactionHandlingAmountDue
                .add(safekeepingAmountDue);
    }

}
