package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType9DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.model.BillingSfvalRgMonthly;
import com.services.billingservice.service.Core9Service;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.service.SfValRgMonthlyService;
import com.services.billingservice.service.SkTranService;
import com.services.billingservice.utils.ConvertBigDecimalUtil;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core9ServiceImpl implements Core9Service {

    private final MockKycCustomerService mockKycCustomerService;
    private final MockFeeParameterService mockFeeParameterService;
    private final SkTranService skTranService;
    private final SfValRgMonthlyService sfValRgMonthlyService;

    @Override
    public List<CoreType9DTO> calculate(String category, String type, String monthYear) {
        // TODO: Call service Kyc Customer
        List<MockKycCustomerDTO> kycCustomerDTOList = mockKycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Get value by name TRANSACTION_HANDLING from service Fee Parameter
        BigDecimal transactionHandlingFee = mockFeeParameterService.getValueByName(FeeParameterNameConstant.TRANSACTION_HANDLING_IDR);

        // TODO: Get value by name VAT from service Fee Parameter
        BigDecimal vatFee = mockFeeParameterService.getValueByName(FeeParameterNameConstant.VAT);

        // TODO: Initialization variable
        int transactionHandlingValueFrequency;
        BigDecimal transactionHandlingAmountDue;
        BigDecimal safekeepingValueFrequency;
        BigDecimal safekeepingAmountDue;
        BigDecimal totalAmountDueBeforeTax;
        String vatFeeStr = ConvertBigDecimalUtil.formattedTaxFee(Double.parseDouble(String.valueOf(vatFee)));
        BigDecimal vatAmountDue;
        BigDecimal totalAmountDueAfterTax;
        List<CoreType9DTO> coreType9DTOList = new ArrayList<>();

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

            // TODO: SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO:  Sf Val RG Monthly
            List<BillingSfvalRgMonthly> sfValRgMonthlyList = sfValRgMonthlyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Transaction Handling Value Frequency
            transactionHandlingValueFrequency = getTransactionHandlingFrequency(aid, skTransactionList);

            // TODO: Transaction Handling Amount Due
            transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(transactionHandlingValueFrequency, transactionHandlingFee);

            // TODO: Safekeeping Value Frequency
            safekeepingValueFrequency = getSafekeepingValueFrequency(aid, sfValRgMonthlyList);

            // TODO: Safekeeping Amount Due, safekeeping masih string, lalu kita process untuk dibagi 100 persen dahulu
            safekeepingAmountDue = calculateSafekeepingAmountDue(safekeepingValueFrequency, customerFee);

            // TODO: Total Amount Due Before Tax
            totalAmountDueBeforeTax = calculateTotalAmountDueBeforeTax(transactionHandlingAmountDue, safekeepingAmountDue);

            // TODO: VAT Amount Due
            vatAmountDue = calculateVatAmountDue(totalAmountDueBeforeTax, vatFee);

            // TODO: Total Amount Due After Tax
            totalAmountDueAfterTax = calculateTotalAmountDueAfterTax(totalAmountDueBeforeTax, vatAmountDue);

            CoreType9DTO coreType9DTO = CoreType9DTO.builder()
                    .transactionHandlingValueFrequency(String.valueOf(transactionHandlingValueFrequency))
                    .transactionHandlingFee(String.valueOf(transactionHandlingFee))
                    .transactionHandlingAmountDue(String.valueOf(transactionHandlingAmountDue))
                    .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                    .safekeepingFee(customerFee)
                    .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                    .totalAmountDueBeforeTax(String.valueOf(totalAmountDueBeforeTax))
                    .vatFee(vatFeeStr)
                    .vatAmountDue(String.valueOf(vatAmountDue))
                    .totalAmountDueAfterTax(String.valueOf(totalAmountDueAfterTax))
                    .build();

            coreType9DTOList.add(coreType9DTO);
        }

        return coreType9DTOList;
    }


    private static int getTransactionHandlingFrequency(String aid, List<BillingSKTransaction> skTransactionList) {
        int totalTransactionHandlingFrequency;

        if (0 == skTransactionList.size()) {
            totalTransactionHandlingFrequency = 0;
        } else {
            totalTransactionHandlingFrequency = skTransactionList.size();
        }

        log.info("[Core Type 9] Transaction Handling Frequency with Aid : {}, is : {}", aid, totalTransactionHandlingFrequency);
        return totalTransactionHandlingFrequency;
    }

    private static BigDecimal calculateTransactionHandlingAmountDue(int transactionHandlingValueFrequency, BigDecimal transactionHandlingFee) {
        return new BigDecimal(transactionHandlingValueFrequency)
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

        log.info("[Core Type 9] Safekeeping Value Frequency with Aid : {}, is : {}", aid, safeValueFrequency);
        return safeValueFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(BigDecimal safekeepingValueFrequency, String safekeepingFee) {
        BigDecimal safeKeepingAfterDivide100Percentage = new BigDecimal(safekeepingFee)
                .divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);

        return safekeepingValueFrequency
                .multiply(safeKeepingAfterDivide100Percentage)
                .divide(new BigDecimal(12), 0, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDueBeforeTax(BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue) {
        return transactionHandlingAmountDue
                .add(safekeepingAmountDue);
    }

    private static BigDecimal calculateVatAmountDue(BigDecimal totalAmountDueBeforeTax, BigDecimal vatFee) {
        return totalAmountDueBeforeTax
                .multiply(vatFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDueAfterTax(BigDecimal totalAmountDueBeforeTax, BigDecimal vatAmountDue) {
        return totalAmountDueBeforeTax
                .add(vatAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

}
