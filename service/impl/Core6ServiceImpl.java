package com.services.billingservice.service.impl;

import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType6DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core6ServiceImpl implements Core6Service {

    private final MockKycCustomerService kycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final SkTranService skTranService;
    private final SfValRgDailyService sfValRgDailyService;
    private final KseiSafeService kseiSafeService;

    @Override
    public List<CoreType6DTO> calculate(String category, String type, String monthYear) {

        // TODO: Retrieve Kyc Customer data via KycCustomerService
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Retrieve Fee Parameter
        // 23.000 (Fee Parameter BIS4 Fee)
        BigDecimal bis4Fee = feeParameterService.getValueByName(FeeParameterNameConstant.KSEI);
        // 22.200 (Fee Parameter KSEI Fee)
        BigDecimal kseiTransactionFee = feeParameterService.getValueByName(FeeParameterNameConstant.KSEI);
        // 11 % (Fee Parameter VAT)
        BigDecimal vatFee = feeParameterService.getValueByName(FeeParameterNameConstant.VAT);


        // TODO: Initialization variable
        BigDecimal safekeepingValueFrequency;
        BigDecimal safekeepingAmountDue;
        int bis4ValueFrequency;
        BigDecimal bis4AmountDue;
        BigDecimal totalAmountDueBeforeVat;
        BigDecimal vatAmountDue;
        int kseiTransactionValueFrequency;
        BigDecimal kseiTransactionAmountDue;
        BigDecimal kseiSafekeepingAmountDue;
        BigDecimal totalAmountDueAfterVat;
        List<CoreType6DTO> coreType6DTOList = new ArrayList<>();

        // TODO: Retrieve Month and Year data from ConvertDateUtil conversion results
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        // TODO: Process calculate Billing
        for (int i = 1; i <= kycCustomerDTOList.size(); i++) {
            MockKycCustomerDTO kycCustomerDTO = kycCustomerDTOList.get(i);
            String aid = kycCustomerDTO.getAid();
            String kseiSafeCode = kycCustomerDTO.getKseiSafeCode();
            String customerFee = kycCustomerDTO.getCustomerFee();

            // TODO: SK Transaction with AID, Month, and Year
            List<BillingSKTransaction> skTransactionList = skTranService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: SfVal RG Daily with AID, Month, and Year
            List<BillingSfvalRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, monthName, year);

            // TODO: Safekeeping Value Frequency
            safekeepingValueFrequency = getSafekeepingValueFrequency(aid, monthName, year, sfValRgDailyList);

            // TODO: Safekeeping Amount Due
            safekeepingAmountDue = getSafekeepingAmountDue(aid, monthName, year, sfValRgDailyList);

            // TODO: BI-SSSS Value Frequency
            bis4ValueFrequency = getBis4ValueFrequency(aid, monthName, year, skTransactionList);

            // TODO: BI-SSSS Amount Due
            bis4AmountDue = calculateBis4AmountDue(bis4ValueFrequency, bis4Fee);

            // TODO: Total Amount Due Before VAT
            totalAmountDueBeforeVat = calculateTotalBeforeVat(safekeepingAmountDue, bis4AmountDue);

            // TODO: VAT Amount Due
            vatAmountDue = calculateVatAmountDue(totalAmountDueBeforeVat, vatFee);

            // TODO: KSEI Fee - Transaction Value Frequency
            kseiTransactionValueFrequency = getKseiValueFrequency(aid, monthName, year, skTransactionList);

            // TODO: KSEI Fee - Transaction Amount Due
            kseiTransactionAmountDue = calculateKseiTransactionAmountDue(kseiTransactionValueFrequency, kseiTransactionFee);

            // TODO: KSEI Fee - Safekeeping Amount Due
            kseiSafekeepingAmountDue = kseiSafeService.calculateAmountFeeByKseiSafeCodeAndMonthAndYear(kseiSafeCode, monthName, year);

            // TODO: Total Amount Due After VAT
            totalAmountDueAfterVat = calculateTotalAmountDue(
                    totalAmountDueBeforeVat, vatAmountDue,
                    kseiTransactionAmountDue, kseiSafekeepingAmountDue
            );

            CoreType6DTO coreType6DTO = CoreType6DTO.builder()
                    .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                    .safekeepingFee(customerFee)
                    .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                    .bis4ValueFrequency(String.valueOf(bis4ValueFrequency))
                    .bis4Fee(String.valueOf(bis4Fee))
                    .bis4AmountDue(String.valueOf(bis4AmountDue))
                    .totalBeforeVat(String.valueOf(totalAmountDueBeforeVat))
                    .vatFee(String.valueOf(vatFee))
                    .vatAmountDue(String.valueOf(vatAmountDue))
                    .kseiTransactionValueFrequency(String.valueOf(kseiTransactionValueFrequency))
                    .kseiTransactionFee(String.valueOf(kseiTransactionFee))
                    .kseiTransactionAmountDue(String.valueOf(kseiTransactionAmountDue))
                    .kseiSafekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                    .totalAmountDue(String.valueOf(totalAmountDueAfterVat))
                    .build();

            coreType6DTOList.add(coreType6DTO);
        }

        return coreType6DTOList;
    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }

    private static BigDecimal getSafekeepingValueFrequency(String aid, String month, int year, List<BillingSfvalRgDaily> sfvalRgDailyList) {
        BigDecimal safekeepingValueFrequency = sfvalRgDailyList.stream()
                .map(BillingSfvalRgDaily::getMarketValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);

        log.info("[Core Type 6] Safekeeping value frequency with Aid : {}, Month : {}, Year : {}, is : {}", aid, month, year, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal getSafekeepingAmountDue(String aid, String monthName, int year, List<BillingSfvalRgDaily> sfvalRgDailyList) {
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

        log.info("[Core Type 6] Safekeeping Amount Due with Aid : {}, Month : {}, Year : {}, is : {}", aid, monthName, year, safekeepingAmountDue);
        return safekeepingAmountDue;
    }

    private static int getBis4ValueFrequency(String aid, String month, int year, List<BillingSKTransaction> skTransactionList) {

        List<BillingSKTransaction> collect = skTransactionList.stream()
                .filter(skTransaction -> skTransaction.getSystem().equalsIgnoreCase("BI-SSSS"))
                .collect(Collectors.toList());

        int totalBis4ValueFrequency;

        if (0 == collect.size()) {
            totalBis4ValueFrequency = 0;
        } else {
            totalBis4ValueFrequency = collect.size();
        }

        log.info("[Core Type 6] BI-SSSS value frequency with Aid : {}, Month : {}, Year : {}, is : {}", aid, month, year, totalBis4ValueFrequency);
        return totalBis4ValueFrequency;
    }

    private static BigDecimal calculateBis4AmountDue(Integer bis4ValueFrequency, BigDecimal bis4Fee) {
        return new BigDecimal(bis4ValueFrequency)
                .multiply(bis4Fee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalBeforeVat(BigDecimal safekeepingAmountDue, BigDecimal bis4AmountDue) {
        return safekeepingAmountDue
                .add(bis4AmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateVatAmountDue(BigDecimal totalBeforeVat, BigDecimal vatFee) {
        return totalBeforeVat
                .multiply(vatFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static int getKseiValueFrequency(String aid, String month, int year, List<BillingSKTransaction> skTransactionList) {
        List<BillingSKTransaction> collect = skTransactionList.stream()
                .filter(skTransaction -> skTransaction.getSystem().equalsIgnoreCase("CBEST"))
                .collect(Collectors.toList());

        int totalKseiValueFrequency;

        if (0 == collect.size()) {
            totalKseiValueFrequency = 0;
        } else {
            totalKseiValueFrequency = collect.size();
        }

        log.info("[Core Type 6] KSEI value frequency with Aid : {}, Month : {}, Year : {}, is : {}", aid, month, year, totalKseiValueFrequency);
        return totalKseiValueFrequency;
    }

    private static BigDecimal calculateKseiTransactionAmountDue(Integer kseiTransactionValueFrequency, BigDecimal kseiTransactionFee) {
        return new BigDecimal(kseiTransactionValueFrequency)
                .multiply(kseiTransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmountDue(BigDecimal totalBeforeVat, BigDecimal vatAmountDue, BigDecimal kseiTransactionAmountDue, BigDecimal kseiSafekeepingAmountDue) {
        return totalBeforeVat
                .add(vatAmountDue)
                .add(kseiTransactionAmountDue)
                .add(kseiSafekeepingAmountDue)
                .setScale(0, RoundingMode.HALF_UP);
    }

}
