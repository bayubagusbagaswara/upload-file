package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.Core5NotNpwpDTO;
import com.services.billingservice.dto.core.Core5NpwpDTO;
import com.services.billingservice.dto.core.CoreType5DTO;
import com.services.billingservice.dto.fund.BillingFundDTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.service.Core5Service;
import com.services.billingservice.service.KseiSafeService;
import com.services.billingservice.service.SkTranService;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.service.SfValRgDailyService;
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
public class Core5ServiceImpl implements Core5Service {

    private final MockKycCustomerService kycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final SkTranService skTranService;
    private final SfValRgDailyService sfValRgDailyService;
    private final KseiSafeService kseiSafeService;

    @Override
    public List<CoreType5DTO> calculate(String category, String type, String monthYear) {

        // TODO: Retrieve Kyc Customer data via KycCustomerService
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Retrieve data Fee Parameter
        List<String> feeParamNameList = new ArrayList<>();
        feeParamNameList.add(FeeParameterNameConstant.KSEI);
        feeParamNameList.add(FeeParameterNameConstant.BIS4);
        feeParamNameList.add(FeeParameterNameConstant.VAT);

        Map<String, BigDecimal> feeParameterMap = feeParameterService.getValueByNameList(feeParamNameList);
        BigDecimal vatFee = feeParameterMap.get(FeeParameterNameConstant.VAT);
        BigDecimal kseiTransactionFee = feeParameterMap.get(FeeParameterNameConstant.KSEI);
        BigDecimal bis4TransactionFee = feeParameterMap.get(FeeParameterNameConstant.BIS4);

        List<CoreType5DTO> coreType5DTOList = new ArrayList<>();

        // TODO: Retrieve Month and Year data from ConvertDateUtil conversion results
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        // TODO: Process calculate Billing
        for (MockKycCustomerDTO kycCustomerDTO : kycCustomerDTOList) {
            // TODO: Data Kyc Customer
            String aid = kycCustomerDTO.getAid();
            String customerFee = kycCustomerDTO.getCustomerFee();
            String kseiSafeCode = kycCustomerDTO.getKseiSafeCode();
            String billingTemplate = kycCustomerDTO.getBillingTemplate();

            // TODO: SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: SfVal RG Daily
            List<BillingSfvalRgDaily> sfvalRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Retrieve KSEI Safekeeping Amount
            BigDecimal kseiAmountFee = kseiSafeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(kseiSafeCode, monthName, year);

            // TODO: Checking by Billing Template
            if (billingTemplate.equalsIgnoreCase("TEMPLATE_1")) {
                // TODO: calculate No NPWP
                Core5NotNpwpDTO core5NotNpwpDTO = calculateNotNpwp(
                        aid, customerFee, kseiAmountFee, kseiTransactionFee, bis4TransactionFee,
                        skTransactionList, sfvalRgDailyList);

            } else {
                // TODO: calculate with NPWP
                Core5NpwpDTO core5NpwpDTO = calculateWithNpwp(
                        aid, customerFee, vatFee, kseiAmountFee,
                        kseiTransactionFee, bis4TransactionFee, skTransactionList, sfvalRgDailyList
                );
            }

        }

        return coreType5DTOList;
    }

    private Core5NpwpDTO calculateWithNpwp(String aid, String customerFee, BigDecimal vatFee, BigDecimal kseiAmountFee, BigDecimal kseiTransactionFee, BigDecimal bis4TransactionFee, List<BillingSKTransaction> skTransactionList, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        return null;
    }


    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }

    private static BigDecimal getSafekeepingValueFrequency(String aid, List<BillingSfvalRgDaily> sfValRgDailyList) {
        Optional<LocalDate> latestDateOptional = sfValRgDailyList.stream()
                .map(BillingSfvalRgDaily::getDate)
                .max(Comparator.naturalOrder());

        List<BillingSfvalRgDaily> latestEntries = latestDateOptional
                .map(date -> sfValRgDailyList.stream()
                        .filter(entry -> entry.getDate().equals(date))
                        .collect(Collectors.toList())
                )
                .orElseGet(LinkedList::new);

        for (BillingSfvalRgDaily latestEntry : latestEntries) {
            log.info("Latest SfVal RG Daily with Aid : {}," +
                    "Security : {}, " +
                    "Date : {}",
                    latestEntry.getAid(), latestEntry.getSecurityName(), latestEntry.getDate());
        }

        BigDecimal safeValueFrequency;

        if (0 == latestEntries.size()) {
            safeValueFrequency = BigDecimal.ZERO;
        } else {
            safeValueFrequency = latestEntries.stream()
                    .map(BillingSfvalRgDaily::getMarketValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);
        }

        log.info("[Core Type 5] Safekeeping Value Frequency with Aid : {}" +
                "is : {}", aid, safeValueFrequency);
        return safeValueFrequency;
    }

    private static BigDecimal getSafekeepingAmountDue(String aid, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        BigDecimal safekeepingAmountDue;
        if (0 == sfvalRgDailyList.size()) {
            safekeepingAmountDue = BigDecimal.ZERO;
        } else {
            safekeepingAmountDue = sfvalRgDailyList.stream()
                    .map(BillingSfvalRgDaily::getEstimationSkFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP);
        }
        log.info("[Core Type 5] Safekeeping Amount Due with " +
                "Aid : {}" +
                "is : {}", aid, safekeepingAmountDue);

        return safekeepingAmountDue;
    }

    private static Core5NotNpwpDTO calculateNotNpwp(String aid, String customerFee,
                                                    BigDecimal kseiAmountFee,
                                                    BigDecimal kseiTransactionFee, BigDecimal bis4TransactionFee,
                                                    List<BillingSKTransaction> skTransactionList, List<BillingSfvalRgDaily> sfvalRgDailyList) {

        // safekeepingValueFrequency
        BigDecimal safekeepingValueFrequency = getSafekeepingValueFrequency(aid, sfvalRgDailyList);

        // safekeeping fee adalah customer fee
        String safekeepingFee = customerFee;

        // safekeepingAmountDue
        BigDecimal safekeepingAmountDue = getSafekeepingAmountDue(aid, sfvalRgDailyList);

        Map<String, Integer> filterTransactionsType = filterTransactionsType(skTransactionList);
        Integer kseiTransactionValueFrequency = filterTransactionsType.get("ksei");
        Integer bis4TransactionValueFrequency = filterTransactionsType.get("bis4");

        // kseiTransactionAmountDue, calculate(kseiTransactionValueFrequency, kseiTransactionFee)
        BigDecimal kseiTransactionAmountDue = calculateKseiTransactionAmountDue(
                kseiTransactionValueFrequency, kseiTransactionFee
        );

        BigDecimal bis4TransactionAmountDue = calculateBis4TransactionAmountDue(
                bis4TransactionValueFrequency, bis4TransactionFee
        );

        // kseiSafekeepingAmountDue (dari table KSEI Safe)
        BigDecimal kseiSafekeepingAmountDue = kseiAmountFee;

        // calculate total amount due
        BigDecimal totalAmountDue = calculateTotalAmountDue(
          safekeepingAmountDue,
          kseiTransactionAmountDue,
          bis4TransactionAmountDue,
          kseiSafekeepingAmountDue
        );

        return Core5NotNpwpDTO.builder()
                .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                .safekeepingFee(customerFee)
                .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))

                .kseiTransactionValueFrequency(String.valueOf(kseiTransactionValueFrequency))
                .kseiTransactionFee(String.valueOf(kseiTransactionFee))
                .kseiTransactionAmountDue(String.valueOf(kseiTransactionAmountDue))

                .bis4TransactionValueFrequency(String.valueOf(bis4TransactionValueFrequency))
                .bis4TransactionFee(String.valueOf(bis4TransactionFee))
                .bis4TransactionAmountDue(String.valueOf(bis4TransactionAmountDue))

                .kseiSafekeepingAmountDue(String.valueOf(kseiSafekeepingAmountDue))

                .totalAmountDue(String.valueOf(totalAmountDue))
                .build();
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal safekeepingAmountDue, BigDecimal kseiTransactionAmountDue, BigDecimal bis4TransactionAmountDue, BigDecimal kseiSafekeepingAmountDue) {
        return safekeepingAmountDue
                .add(kseiTransactionAmountDue)
                .add(bis4TransactionAmountDue)
                .add(kseiSafekeepingAmountDue);
    }

    private static BigDecimal calculateBis4TransactionAmountDue(Integer bis4TransactionValueFrequency, BigDecimal bis4TransactionFee) {
        return new BigDecimal(bis4TransactionValueFrequency)
                .multiply(bis4TransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateKseiTransactionAmountDue(Integer kseiTransactionValueFrequency, BigDecimal kseiTransactionFee) {
        return new BigDecimal(kseiTransactionValueFrequency)
                .multiply(kseiTransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }


    private static Map<String, Integer> filterTransactionsType(List<BillingSKTransaction> skTransactionList) {
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (BillingSKTransaction skTransaction : skTransactionList) {
            String settlementSystem = skTransaction.getSystem();
            if (settlementSystem != null) {
                if ("CBEST".equalsIgnoreCase(settlementSystem)) {
                    transactionCBESTTotal++;
                } else if ("BI-SSSS".equalsIgnoreCase(settlementSystem)) {
                    transactionBIS4Total++;
                }
            }
        }
        log.info("Total KSEI : {}", transactionCBESTTotal);
        log.info("Total BI-S4 : {}", transactionBIS4Total);

        Map<String, Integer> resultFilter = new HashMap<>();
        resultFilter.put("ksei", transactionCBESTTotal);
        resultFilter.put("bis4", transactionBIS4Total);

        return resultFilter;
    }

}
