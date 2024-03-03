package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType4DTO;
import com.services.billingservice.dto.core.CoreType4EbDTO;
import com.services.billingservice.dto.core.CoreType4ItamaDTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingKseiSafe;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.service.*;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core4ServiceImpl implements Core4Service {

    private final MockKycCustomerService kycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final SkTranService skTranService;
    private final SfValRgDailyService sfValRgDailyService;
    private final KseiSafeService kseiSafeService;

    @Override
    public List<CoreType4DTO> calculate(String category, String type, String monthYear) {

        // TODO: Call service Kyc Customer
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Get value by name KSEI from service Fee Parameter
        BigDecimal kseiTransactionFee= feeParameterService.getValueByName(FeeParameterNameConstant.KSEI);

        // TODO: Get value by name VAT from service Fee Parameter
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameterNameConstant.VAT);

        // TODO: Initialization variable
        List<CoreType4DTO> coreType4DTOList = new ArrayList<>();

        // TODO: Convert Month and Year
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        for (MockKycCustomerDTO kycCustomerDTO : kycCustomerDTOList) {
            // TODO: Data Kyc Customer
            String aid = kycCustomerDTO.getAid();
            String kseiSafeCode = kycCustomerDTO.getKseiSafeCode();
            String customerFee = kycCustomerDTO.getCustomerFee();
            String billingTemplate = kycCustomerDTO.getBillingTemplate();

            // TODO: SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: SfVal RG Daily
            List<BillingSfvalRgDaily> sfvalRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: KSEI Safekeeping
            BillingKseiSafe kseiSafe = kseiSafeService.getByKseiSafeCode(kseiSafeCode);
            BigDecimal kseiSafeAmount = kseiSafeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(
                    kseiSafeCode, monthName, year
            );

            // TODO: Checking by BILLING TEMPLATE
            if (kycCustomerDTO.getBillingTemplate().equalsIgnoreCase("TEMPLATE_5")) {
                // TODO: Data EB (Template 5)
                CoreType4EbDTO resultEB = calculateEB(
                        kseiSafeAmount, kseiTransactionFee, aid,
                        monthName, year, skTransactionList);

                CoreType4DTO coreType4EbDTO = CoreType4DTO.builder()
                        .aid(aid)
                        .billingTemplate(billingTemplate)
                        .period(monthYear)
                        .kseiSafekeepingAmountDue(resultEB.getKseiSafekeepingAmountDue())
                        .kseiTransactionValueFrequency(resultEB.getKseiTransactionValueFrequency())
                        .kseiTransactionFee(resultEB.getKseiTransactionFee())
                        .kseiTransactionAmountDue(resultEB.getKseiTransactionAmountDue())
                        .totalAmountDueEB(resultEB.getTotalAmountDue())
                        .build();

                coreType4DTOList.add(coreType4EbDTO);
            } else {
                // TODO: Data Itama (Template 3)
                CoreType4ItamaDTO resultItama = calculateItama(
                        aid, monthName, year,
                        customerFee, vatFee,
                        sfvalRgDailyList);

                CoreType4DTO coreTypeItama4 = CoreType4DTO.builder()
                        .aid(aid)
                        .billingTemplate(billingTemplate)
                        .period(monthYear)
                        .safekeepingFrequency(resultItama.getSafekeepingValueFrequency())
                        .safekeepingFee(resultItama.getSafekeepingFee())
                        .safekeepingAmountDue(resultItama.getSafekeepingAmountDue())
                        .vatFee(resultItama.getVatFee())
                        .vatAmountDue(resultItama.getVatAmountDue())
                        .totalAmountDueItama(resultItama.getTotalAmountDue())
                        .build();

                coreType4DTOList.add(coreTypeItama4);

            }
        }

        return coreType4DTOList;
    }

    @Override
    public Map<String, List<Object>> calculateTest(String category, String type, String monthYear) {
        // TODO: Call service Kyc Customer
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Get value by name KSEI from service Fee Parameter
        BigDecimal kseiTransactionFee= feeParameterService.getValueByName(FeeParameterNameConstant.KSEI);

        // TODO: Get value by name VAT from service Fee Parameter
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameterNameConstant.VAT);

        // TODO: Initialization object to collect data EB and Itama
        List<CoreType4EbDTO> coreType4EbDTOList = new ArrayList<>();
        List<CoreType4ItamaDTO> coreType4ItamaDTOList = new ArrayList<>();

        // TODO: Initialization object Map
        Map<String, List<Object>> mapObject = new HashMap<>();

        // TODO: Convert Month and Year
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        for (int i = 1; i <= kycCustomerDTOList.size(); i++) {
            // TODO: Data Kyc Customer
            MockKycCustomerDTO kycCustomerDTO = kycCustomerDTOList.get(i);
            String aid = kycCustomerDTO.getAid();
            String kseiSafeCode = kycCustomerDTO.getKseiSafeCode();
            String customerFee = kycCustomerDTO.getCustomerFee();

            // TODO: SK Transaction
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: SfVal RG Daily
            List<BillingSfvalRgDaily> sfvalRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: KSEI Safekeeping
            BigDecimal kseiSafeAmount = kseiSafeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(
                    kseiSafeCode, monthName, year
            );

            /**
             * lebih baik kita bedakan object untuk EB dan ITAMA
             */
            if (kycCustomerDTO.getBillingTemplate().equalsIgnoreCase("TEMPLATE_5")) {
                // TODO: Data EB (Template 5)
                CoreType4EbDTO resultEB = calculateEB(
                        kseiSafeAmount, kseiTransactionFee, aid,
                        monthName, year, skTransactionList);
                coreType4EbDTOList.add(resultEB);
            } else {
                // TODO: Data Itama (Template 3)
                CoreType4ItamaDTO resultItama = calculateItama(
                        aid, monthName, year,
                        customerFee, vatFee,
                        sfvalRgDailyList);
                coreType4ItamaDTOList.add(resultItama);
            }

            mapObject.put("EB", Collections.singletonList(coreType4EbDTOList));
            mapObject.put("ITAMA", Collections.singletonList(coreType4ItamaDTOList));
        }

        return mapObject;
    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }

    private CoreType4EbDTO calculateEB(BigDecimal kseiSafeAmount, BigDecimal kseiTransactionFee, String aid,
                                       String month, Integer year, List<BillingSKTransaction> skTransactionList) {

        // BigDecimal kseiSafekeepingAmountDue = calculateKseiSafekeepingAmountDue(kseiSafe);

        Integer kseiTransactionValueFrequency = getTransactionValueFrequency(aid, month, year, skTransactionList);

        BigDecimal kseiTransactionAmountDue = calculateKseiTransactionAmountDue(kseiTransactionFee, kseiTransactionValueFrequency);

        BigDecimal totalAmountDue = calculateTotalAmountDueEB(kseiSafeAmount, kseiTransactionAmountDue);

        return CoreType4EbDTO.builder()
                .kseiSafekeepingAmountDue(String.valueOf(kseiSafeAmount))
                .kseiTransactionValueFrequency(String.valueOf(kseiTransactionValueFrequency))
                .kseiTransactionFee(String.valueOf(kseiTransactionFee))
                .kseiTransactionAmountDue(String.valueOf(kseiTransactionAmountDue))
                .totalAmountDue(String.valueOf(totalAmountDue))
                .build();
    }

    private CoreType4ItamaDTO calculateItama(String aid, String month, Integer year,
                                             String safekeepingFee, BigDecimal vatFee,
                                             List<BillingSfvalRgDaily> sfvalRgDailyList) {
        BigDecimal safekeepingValueFrequency = getSafekeepingValueFrequency(aid, month, year, sfvalRgDailyList);

        BigDecimal safekeepingAmountDue = calculateSafekeepingAmountDue(aid, month, year, sfvalRgDailyList);

        BigDecimal vatAmountDue = calculateVatAmountDue(safekeepingAmountDue, vatFee);

        BigDecimal totalAmountDue = calculateTotalAmountDueItama(safekeepingAmountDue, vatAmountDue);

        return CoreType4ItamaDTO.builder()
                .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                .safekeepingFee(String.valueOf(safekeepingFee))
                .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                .vatFee(String.valueOf(vatFee))
                .vatAmountDue(String.valueOf(vatAmountDue))
                .totalAmountDue(String.valueOf(totalAmountDue))
                .build();
    }

    private static Integer getTransactionValueFrequency(String aid, String month, Integer year, List<BillingSKTransaction> skTransactionList) {
        int totalTransactionHandling;

        if (0 == skTransactionList.size()) {
            totalTransactionHandling = 0;
        } else {
            totalTransactionHandling = skTransactionList.size();
        }

        log.info("[Core Type 4] Total Transaction Handling with AID : {}, Month :{}, Year : {} is : {}", aid, month, year, totalTransactionHandling);
        return totalTransactionHandling;
    }

    private static BigDecimal calculateKseiTransactionAmountDue(BigDecimal kseiTransactionFee, Integer transactionValueFrequency) {
        return kseiTransactionFee
                .multiply(new BigDecimal(transactionValueFrequency))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDueEB(BigDecimal kseiSafekeepingAmountDue, BigDecimal kseiTransactionAmountDue) {
        return kseiSafekeepingAmountDue
                .add(kseiTransactionAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal getSafekeepingValueFrequency(String aid, String month, Integer year, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        BigDecimal safeKeepingFrequency = sfvalRgDailyList.stream()
                .map(BillingSfvalRgDaily::getMarketValue)
                .reduce((first, second) -> second).orElse(BigDecimal.ZERO)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 4] Safekeeping Value Frequency with Aid : {}, Month : {}, Year : {} is : {}", aid, month, year, safeKeepingFrequency);

        return safeKeepingFrequency;
    }

    private static BigDecimal calculateSafekeepingAmountDue(String aid, String month, Integer year, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        BigDecimal safekeepingAmountDue;

        if (0 == sfvalRgDailyList.size()) {
            safekeepingAmountDue = BigDecimal.ZERO;
        } else {
            safekeepingAmountDue = sfvalRgDailyList.stream()
                    .map(BillingSfvalRgDaily::getEstimationSkFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
            ;
        }

        return safekeepingAmountDue;
    }

    private static BigDecimal calculateVatAmountDue(BigDecimal safekeepingAmountDue, BigDecimal vatFee) {
        return safekeepingAmountDue
                .multiply(vatFee)
                .setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDueItama(BigDecimal safekeepingAmountDue, BigDecimal vatAmountDue) {
        return safekeepingAmountDue
                .add(vatAmountDue);
    }

}
