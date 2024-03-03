package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType2DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.service.Core2Service;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.service.SfValRgDailyService;
import com.services.billingservice.service.SkTranService;
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
public class Core2ServiceImpl implements Core2Service {

    private final MockKycCustomerService kycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final SkTranService skTranService;
    private final SfValRgDailyService sfValRgDailyService;

    @Override
    public List<CoreType2DTO> calculate(String category, String type, String monthYear) {

        // TODO: Call service Kyc Customer
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Get value by name TRANSACTION_HANDLING
        BigDecimal transactionFee = feeParameterService.getValueByName(FeeParameterNameConstant.TRANSACTION_HANDLING_IDR);
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameterNameConstant.VAT);

        // TODO: Initialization variable
        BigDecimal safekeepingValueFrequency;
        BigDecimal safekeepingAmountDue;
        int transactionValueFrequency;
        BigDecimal transactionAmountDue;
        BigDecimal totalAmountBeforeVAT;
        BigDecimal vatAmountDue;
        BigDecimal totalAmountDue;
        List<CoreType2DTO> coreType2List = new ArrayList<>();

        // TODO: Convert Month and Year
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        // TODO: Process calculate Billing
        for (int i = 1; i <= kycCustomerDTOList.size(); i++) {

            // TODO: Data Kyc Customer
            MockKycCustomerDTO kycCustomerDTO = kycCustomerDTOList.get(i);
            String aid = kycCustomerDTO.getAid();
            BigDecimal minimumFee = new BigDecimal(kycCustomerDTO.getMinimumFee());
            String customerFee = kycCustomerDTO.getCustomerFee();

            // TODO: SfVal RG Daily
            List<BillingSfvalRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Safekeeping Value Frequency
            safekeepingValueFrequency = getSafekeepingValueFrequency(aid, monthName, year, sfValRgDailyList);

            // TODO: Safekeeping Amount Due
            safekeepingAmountDue = getSafekeepingAmountDue(aid, monthName, year, minimumFee, sfValRgDailyList);

            // TODO: Transaction Value Frequency
            transactionValueFrequency = getTransactionValueFrequency(aid, monthName, year, skTransactionList);

            // TODO: Transaction Amount Due
            transactionAmountDue = calculateTransactionAmountDue(transactionValueFrequency, transactionFee);

            // TODO: Total Amount Due Before VAT
            totalAmountBeforeVAT = calculateTotalAmountBeforeVAT(transactionAmountDue, safekeepingAmountDue);

            // TODO: VAT Amount Due
            vatAmountDue = calculateVatAmountDue(totalAmountBeforeVAT, vatFee);

            // TODO: Total Amount Due
            totalAmountDue = calculateTotalAmountDue(totalAmountBeforeVAT, vatAmountDue);

            CoreType2DTO coreType2DTO = CoreType2DTO.builder()
                    .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                    .safekeepingFee(String.valueOf(customerFee))
                    .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                    .transactionValueFrequency(String.valueOf(transactionValueFrequency))
                    .transactionFee(String.valueOf(transactionFee))
                    .transactionAmountDue(String.valueOf(transactionAmountDue))
                    .totalAmountBeforeVAT(String.valueOf(totalAmountBeforeVAT))
                    .vatFee(String.valueOf(vatFee))
                    .vatAmountDue(String.valueOf(vatAmountDue))
                    .totalAmountDue(String.valueOf(totalAmountDue))
                    .build();

            coreType2List.add(coreType2DTO);
        }

        return coreType2List;
    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }

    private static int getTransactionValueFrequency(String aid, String month, Integer year, List<BillingSKTransaction> skTransactionList) {
        Integer totalTransactionHandling;

        if (0 == skTransactionList.size()) {
            totalTransactionHandling = 0;
        } else {
            totalTransactionHandling = skTransactionList.size();
        }

        log.info("[Core Type 2] Total Transaction Handling with Aid : {}, Month : {}, Year : {} is : {}", aid, month, year, totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateTransactionAmountDue(Integer transactionValueFrequency, BigDecimal transactionFee) {
        return transactionFee
                .multiply(new BigDecimal(transactionValueFrequency))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal getSafekeepingValueFrequency(String aid, String month, Integer year, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        Optional<LocalDate> latestDateOptional = sfvalRgDailyList.stream()
                .map(BillingSfvalRgDaily::getDate)
                .max(Comparator.naturalOrder());

        log.info("Latest Date Optional : {}", latestDateOptional);

        List<BillingSfvalRgDaily> latestEntries = latestDateOptional.map(localDate -> sfvalRgDailyList.stream()
                .filter(entry -> entry.getDate().equals(localDate))
                .collect(Collectors.toList()))
                .orElseGet(LinkedList::new);

        BigDecimal safeValueFrequency;

        if (0 == latestEntries.size()) {
            safeValueFrequency = BigDecimal.ZERO;
        } else {
            safeValueFrequency = latestEntries.stream()
                    .map(BillingSfvalRgDaily::getMarketValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
            ;
        }

        log.info("[Core Type 2] Safekeeping Value Frequency with Aid : {}, Month : {}, Year : {} is : {}", aid, month, year, safeValueFrequency);
        return safeValueFrequency;
    }

    private static BigDecimal getSafekeepingAmountDue(String aid, String month, Integer year, BigDecimal customerMinimumFee, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        BigDecimal safeAmountDue;

        if (0 == sfvalRgDailyList.size()) {
            safeAmountDue = BigDecimal.ZERO;
        } else {
            safeAmountDue = sfvalRgDailyList.stream()
                    .map(BillingSfvalRgDaily::getEstimationSkFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
            ;
        }

        log.info("[Core Type 2] Safekeeping Amount Due with AID : {}, Month : {}, Year : {} is : {}", aid, month, year, safeAmountDue);
        return safeAmountDue.compareTo(customerMinimumFee) < 0 ? customerMinimumFee : safeAmountDue;
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

    private static BigDecimal calculateTotalAmountDue(BigDecimal totalBeforeVAT, BigDecimal vatAmountDue) {
        return totalBeforeVAT
                .add(vatAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }
}
