package com.services.billingservice.service.impl;

import com.services.billingservice.dto.core.CoreType3DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.service.Core3Service;
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
public class Core3ServiceImpl implements Core3Service {

    private final MockKycCustomerService kycCustomerService;
    private final SfValRgDailyService sfValRgDailyService;

    @Override
    public List<CoreType3DTO> calculate(String category, String type, String monthYear) {

        // TODO: Call service Kyc Customer
        List<MockKycCustomerDTO> kycCustomerDTOList = kycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Initialization variable
        BigDecimal safekeepingValueFrequency;
        BigDecimal safekeepingAmountDue;
        List<CoreType3DTO> coreType3List = new ArrayList<>();

        // TODO: Convert Month and Year
        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String month = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        // TODO: Process calculate Billing
        for (int i = 1; i <= kycCustomerDTOList.size(); i++) {

            // TODO: Data Kyc Customer
            MockKycCustomerDTO kycCustomerDTO = kycCustomerDTOList.get(i);
            String aid = kycCustomerDTO.getAid();
            String customerFee = kycCustomerDTO.getCustomerFee();
            String journal = kycCustomerDTO.getJournal();

            // TODO: SfVal RG Daily
            List<BillingSfvalRgDaily> sfValRgDailyList = sfValRgDailyService.getAllByAidAndMonthAndYear(aid, month, year);

            // TODO: Safekeeping Value Frequency
            safekeepingValueFrequency = getSafekeepingValueFrequency(aid, month, year, sfValRgDailyList);

            // TODO: Safekeeping Amount Due
            safekeepingAmountDue = getSafekeepingAmountDue(aid, month, year, sfValRgDailyList);

            CoreType3DTO coreType3DTO = CoreType3DTO.builder()
                    .safekeepingValueFrequency(String.valueOf(safekeepingValueFrequency))
                    .safekeepingFee(String.valueOf(customerFee))
                    .safekeepingAmountDue(String.valueOf(safekeepingAmountDue))
                    .safekeepingCreditTo(journal)
                    .build();

            coreType3List.add(coreType3DTO);
        }

        return coreType3List;
    }

    @Override
    public String calculate1(String category, String type, String monthYear) {
        return null;
    }


    private static BigDecimal getSafekeepingValueFrequency(String aid, String month, Integer year, List<BillingSfvalRgDaily> sfValRgDailyList) {
        Optional<LocalDate> latestDateOptional = sfValRgDailyList.stream()
                .map(BillingSfvalRgDaily::getDate)
                .max(Comparator.naturalOrder());

        log.info("[Core Type 3] Latest Date Optional : {}", latestDateOptional);

        List<BillingSfvalRgDaily> latestEntries = latestDateOptional
                .map(localDate -> sfValRgDailyList.stream()
                        .filter(entry -> entry.getDate().equals(localDate))
                        .collect(Collectors.toList()))
                .orElseGet(LinkedList::new);

        BigDecimal safekeepingValueFrequency;

        if (0 == latestEntries.size()) {
            safekeepingValueFrequency = BigDecimal.ZERO;
        } else {
            safekeepingValueFrequency = latestEntries.stream()
                    .map(BillingSfvalRgDaily::getMarketValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
            ;
        }

        log.info("[Core Type 3] Safekeeping Value Frequency with Aid : {}, Month : {}, Year : {} is : {}", aid, month, year, safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal getSafekeepingAmountDue(String aid, String month, int year, List<BillingSfvalRgDaily> sfValRgDailyList) {
        BigDecimal safekeepingAmountDue;

        if (0 == sfValRgDailyList.size()) {
            safekeepingAmountDue = BigDecimal.ZERO;
        } else {
            safekeepingAmountDue = sfValRgDailyList.stream()
                    .map(BillingSfvalRgDaily::getEstimationSkFee)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
            ;
        }

        log.info("[Core Type 4] Safekeeping Amount Due with Aid : {}, Month : {}, Year : {} is : {}", aid, month, year, safekeepingAmountDue);
        return safekeepingAmountDue;
    }
}
