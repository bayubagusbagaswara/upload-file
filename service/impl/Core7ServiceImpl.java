package com.services.billingservice.service.impl;

import com.services.billingservice.dto.core.CoreType7DTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSfvalRgMonthly;
import com.services.billingservice.service.*;
import com.services.billingservice.service.mock.MockKycCustomerService;
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

/**
 * di file BILMONTH sudah ditambahkan AID (portfolio code) 17MUFG dan 17GUDH
 * selanjutnya kita ambil semua data berdasarkan 17MUFG dan 17GUDH
 * Jadi type 7 ini ada 2 AID, yakni 17MUFG dan 17GUDH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Core7ServiceImpl implements Core7Service {

    private static String MUFG = "17MUFG";
    private static String GUDH = "17GUDH";

    private final MockKycCustomerService mockKycCustomerService;
    private final SkTranService skTranService;
    private final SfValRgMonthlyService sfValRgMonthlyService;
    private final KseiSafeService kseiSafeService;

    @Override
    public List<CoreType7DTO> calculate(String category, String type, String monthYear) {

        List<MockKycCustomerDTO> kycCustomerDTOList = mockKycCustomerService.getAllByBillingCategoryAndBillingType(
                category, type
        );

        BigDecimal safekeepingAmountDue;
        BigDecimal subTotal;
        double vatFee = 0.11;
        BigDecimal vatAmountDue;
        BigDecimal kseiSafekeepingAmountDue;
        Integer kseiTransactionValueFrequency;
        BigDecimal kseiTransactionFee = new BigDecimal(22_200);
        BigDecimal kseiTransactionAmountDue;
        BigDecimal totalAmountDue;

        List<CoreType7DTO> coreType7DTOList = new ArrayList<>();

        Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(monthYear);
        String monthName = monthYearMap.get("monthName");
        int year = Integer.parseInt(monthYearMap.get("year"));

        for (MockKycCustomerDTO kycCustomerDTO : kycCustomerDTOList) {
            // yang masuk perulangan adalah AID 17MUFG
            String aid = kycCustomerDTO.getAid();
            String kseiSafeCode = kycCustomerDTO.getKseiSafeCode();

            // ada 2 data, MUFG dan GUDH
            List<BillingSfvalRgMonthly> sfvalRgMonthlyList = sfValRgMonthlyService.getAllByAidAndMonthAndYear(
                    aid,
                    monthName,
                    year
            );

            // TODO: ini masih salah, karena harusnya 3 data bulan terakhir
            safekeepingAmountDue = sumSafekeepingAmountDue(aid, sfvalRgMonthlyList);

            // buat jaga-jaga jika nanti pada sub total akan bertambah jenis transaksinya
            List<BigDecimal> values = new ArrayList<>();
            values.add(safekeepingAmountDue);

            subTotal = calculateSubTotal(values);

            vatAmountDue = calculateVatAmountDue(subTotal, vatFee);

            // ambil data 3 bulan terakhir
            // TODO: Bikin service di ksei safekeeping untuk mengambil data 3 bulan terakhir
            // get3DataLatest(String kseiSafeCode, String month, int year)
            kseiSafekeepingAmountDue = BigDecimal.ZERO;

            kseiTransactionValueFrequency = 0;

            kseiTransactionAmountDue = calculateKseiTransactionAmountDue(
                    kseiTransactionValueFrequency,
                    kseiTransactionFee
            );

            totalAmountDue = BigDecimal.ZERO;

            CoreType7DTO coreType7DTO = CoreType7DTO.builder()
                    .safekeepingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(safekeepingAmountDue))
                    .subTotal(ConvertBigDecimalUtil.formattedBigDecimalToString(subTotal))

                    .vatFee(ConvertBigDecimalUtil.formattedTaxFee(vatFee))
                    .vatAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(vatAmountDue))

                    .kseiSafekeepingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(kseiSafekeepingAmountDue))
                    .kseiTransactionValueFrequency(String.valueOf(kseiTransactionValueFrequency))
                    .kseiTransactionFee(ConvertBigDecimalUtil.formattedBigDecimalToString(kseiTransactionFee))
                    .kseiTransactionAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(kseiTransactionAmountDue))

                    .totalAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(totalAmountDue))

                    .build();

            coreType7DTOList.add(coreType7DTO);
        }

        return coreType7DTOList;
    }

    private BigDecimal calculateKseiTransactionAmountDue(Integer kseiTransactionValueFrequency, BigDecimal kseiTransactionFee) {
        return new BigDecimal(kseiTransactionValueFrequency)
                .multiply(kseiTransactionFee)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVatAmountDue(BigDecimal subTotal, double vatFee) {
        return subTotal
                .multiply(new BigDecimal(vatFee))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal sumSafekeepingAmountDue(String aid, List<BillingSfvalRgMonthly> sfvalRgMonthlyList) {
        BigDecimal safekeepingValueFrequency;

        if (0 == sfvalRgMonthlyList.size()) {
            safekeepingValueFrequency = BigDecimal.ZERO;
        } else {
            Optional<LocalDate> latestDateOptional = sfvalRgMonthlyList.stream()
                    .map(BillingSfvalRgMonthly::getDate)
                    .max(Comparator.naturalOrder());

            log.info("Latest Date Optional : {}", latestDateOptional);

            List<BillingSfvalRgMonthly> latestEntries = latestDateOptional
                    .map(date -> sfvalRgMonthlyList.stream()
                            .filter(entry -> entry.getDate().equals(date))
                            .collect(Collectors.toList())
                    ).orElseGet(LinkedList::new);

            safekeepingValueFrequency = latestEntries.stream()
                    .map(BillingSfvalRgMonthly::getMarketValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
                    ;
        }
        log.info("[Core Type 7] SUM Safekeeping Value Frequency is : {}", safekeepingValueFrequency);
        return safekeepingValueFrequency;
    }

    private static BigDecimal calculateSubTotal(List<BigDecimal> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP);
    }

}
