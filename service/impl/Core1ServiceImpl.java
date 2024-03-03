package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType1DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.service.*;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.utils.ConvertBigDecimalUtil;
import com.services.billingservice.utils.ConvertDateUtil;
import com.services.billingservice.utils.StringUtil;
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
public class Core1ServiceImpl implements Core1Service {

    private final MockKycCustomerService kycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final SkTranService skTranService;
    private final SfValRgDailyService sfValRgDailyService;

    @Override
    public List<CoreType1DTO> calculate(String category, String type, String monthYear) {

        // TODO: Cleansing data category, type, monthYear
        String categoryUpperCase = category.toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(type).toUpperCase();

        // TODO: Call service Kyc Customer
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(categoryUpperCase, typeUpperCase);

        // TODO: Get value by name VAT and TRANSACTION_HANDLING from service Fee Parameter
        List<String> feeParamNameList = new ArrayList<>();
        feeParamNameList.add(FeeParameterNameConstant.TRANSACTION_HANDLING_IDR);
        feeParamNameList.add(FeeParameterNameConstant.VAT);

        // TODO: Fee Parameter Map
        Map<String, BigDecimal> feeParameterMap = feeParameterService.getValueByNameList(feeParamNameList);

        // TODO: get value fee from feeParameterMapList
        BigDecimal transactionHandlingFee = feeParameterMap.get(FeeParameterNameConstant.TRANSACTION_HANDLING_USD);
        BigDecimal vatFee = feeParameterMap.get(FeeParameterNameConstant.VAT);
        String vatFeeStr = ConvertBigDecimalUtil.formattedTaxFeeBigDecimal(vatFee);

        // TODO: Initialization variable
        Integer transactionHandlingValueFrequency;
        BigDecimal transactionHandlingAmountDue;
        BigDecimal safekeepingValueFrequency;
        BigDecimal safekeepingAmountDue;
        BigDecimal totalAmountBeforeVAT;
        BigDecimal vatAmountDue;
        BigDecimal totalAmountDue;
        List<CoreType1DTO> coreType1List = new ArrayList<>();

        // TODO: Get data Month and Year
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        // TODO: Process calculate billing
        for (MockKycCustomerDTO kycCustomerDTO : kycCustomerDTOList) {
            // TODO: Data Kyc Customer
            String aid = kycCustomerDTO.getAid();
            String customerFee = kycCustomerDTO.getCustomerFee();

            // TODO: SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: SfVal RG Daily
            List<BillingSfvalRgDaily> rgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Transaction Handling Value Frequency
            transactionHandlingValueFrequency = getTransactionHandlingValueFrequency(aid, monthName, year, skTransactionList);

            // TODO: Transaction Handling Amount Due
            transactionHandlingAmountDue = calculateTransactionHandlingAmountDue(transactionHandlingFee, transactionHandlingValueFrequency);

            // TODO: Safekeeping Value Frequency
            safekeepingValueFrequency = getSafekeepingValueFrequency(aid, monthName, year, rgDailyList);

            // TODO: Safekeeping Amount Due
            safekeepingAmountDue = getSafekeepingAmountDue(aid, monthName, year, rgDailyList);

            // TODO: Total Amount Due Before VAT
            totalAmountBeforeVAT = calculateTotalAmountBeforeVAT(transactionHandlingAmountDue, safekeepingAmountDue);

            // TODO: VAT Amount Due
            vatAmountDue = calculateVatAmountDue(totalAmountBeforeVAT, vatFee);

            // TODO: Total Amount Due
            totalAmountDue = calculateTotalAmountDue(totalAmountBeforeVAT, vatAmountDue);

            CoreType1DTO coreType1DTO = CoreType1DTO.builder()
                    .billingNumber("")
                    .category(category)
                    .type(type)
                    .transactionHandlingValueFrequency(String.valueOf(transactionHandlingValueFrequency))
                    .transactionHandlingFee(ConvertBigDecimalUtil.formattedBigDecimalToString(transactionHandlingFee))
                    .transactionHandlingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(transactionHandlingAmountDue))
                    .safekeepingValueFrequency(ConvertBigDecimalUtil.formattedBigDecimalToString(safekeepingValueFrequency))
                    .safekeepingFee(String.valueOf(customerFee))
                    .safekeepingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(safekeepingAmountDue))
                    .totalAmountBeforeVAT(ConvertBigDecimalUtil.formattedBigDecimalToString(totalAmountBeforeVAT))
                    .vatFee(vatFeeStr)
                    .vatAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(vatAmountDue))
                    .totalAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(totalAmountDue))
                    .build();

            coreType1List.add(coreType1DTO);
        }

        // TODO: Save to table Billing, map DTO to Model

        return coreType1List;
    }

    // TODO: CREATE BILLING CORE TYPE 1 DETAIL
    private void insertDetailBilling(List<BillingSfvalRgDaily> sfValRgDailyList) {

        // return is List<BillingCoreDetail>

        for (BillingSfvalRgDaily sfValRgDaily : sfValRgDailyList) {
            LocalDate date = sfValRgDaily.getDate();
            String aid = sfValRgDaily.getAid();
            String securityName = sfValRgDaily.getSecurityName();
            BigDecimal marketValue = sfValRgDaily.getMarketValue();
            BigDecimal estimationSkFee = sfValRgDaily.getEstimationSkFee();
        }

    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }

    @Override
    public String generateFilePdf(String category, String type, String monthYear) {

        // TODO: ambil dari table


        return null;
    }

    private static int getTransactionHandlingValueFrequency(String aid, String month, Integer year, List<BillingSKTransaction> skTransactionList) {
        int totalTransactionHandling;

        if (0 == skTransactionList.size()) {
            totalTransactionHandling = 0;
        } else {
            totalTransactionHandling = skTransactionList.size();
        }

        log.info("[Core Type 1] Transaction Handling Value Frequency with AID : {}, Month : {}, Year : {} is : {}", aid, month, year, totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateTransactionHandlingAmountDue(BigDecimal transactionHandlingFee, Integer transactionHandlingValueFrequency) {
        return transactionHandlingFee
                .multiply(new BigDecimal(transactionHandlingValueFrequency))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal getSafekeepingValueFrequency(String aid, String month, Integer year, List<BillingSfvalRgDaily> rgDailyList) {
        Optional<LocalDate> latestDateOptional = rgDailyList.stream()
                .map(BillingSfvalRgDaily::getDate)
                .max(Comparator.naturalOrder());

        log.info("[Core Type 1] Latest Date Optional : {}", latestDateOptional);

        List<BillingSfvalRgDaily> latestEntries = latestDateOptional
                .map(localDate -> rgDailyList.stream()
                        .filter(entry -> entry.getDate().equals(localDate))
                        .collect(Collectors.toList()))
                .orElseGet(LinkedList::new);

        BigDecimal safeValueFrequency;

        if (0 == latestEntries.size()) {
            safeValueFrequency = BigDecimal.ZERO;
        } else {
            safeValueFrequency= latestEntries.stream()
                    .map(BillingSfvalRgDaily::getMarketValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
            ;
        }

        log.info("[Core Type 1] Safekeeping Value Frequency with AID : {}, Month : {}, Year : {} is {}", aid, month, year, safeValueFrequency);
        return safeValueFrequency;
    }

    private static BigDecimal getSafekeepingAmountDue(String aid, String month, Integer year, List<BillingSfvalRgDaily> rgDailyList) {
        BigDecimal safeAmountDue;

        if (0 == rgDailyList.size()) {
            safeAmountDue = BigDecimal.ZERO;
        } else {
            BigDecimal reduce = rgDailyList.stream()
                    .map(BillingSfvalRgDaily::getEstimationSkFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            safeAmountDue = reduce.setScale(0, RoundingMode.HALF_UP);
        }

        log.info("[Core Type 1] Safekeeping Amount Due with AID : {}, Month : {}, Year : {} is {}", aid, month, year, safeAmountDue);
        return safeAmountDue;
    }

    private static BigDecimal calculateTotalAmountBeforeVAT(BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue) {
        return transactionHandlingAmountDue
                .add(safekeepingAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateVatAmountDue(BigDecimal totalBeforeVAT, BigDecimal vatFee) {
        return totalBeforeVAT
                .multiply(vatFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal totalAmountBeforeVAT, BigDecimal vatAmountDue) {
        return totalAmountBeforeVAT
                .add(vatAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

}
