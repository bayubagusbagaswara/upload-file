package com.services.billingservice.service.impl;

import com.services.billingservice.dto.fund.BillingFundDTO;
import com.services.billingservice.dto.fund.FeeReportRequest;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.service.BillingFundService;
import com.services.billingservice.service.SkTranService;
import com.services.billingservice.utils.ConvertBigDecimalUtil;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillingFundServiceImpl implements BillingFundService {

    private final SkTranService skTransactionService;

    public BillingFundServiceImpl(SkTranService skTransactionService) {
        this.skTransactionService = skTransactionService;
    }

    @Override
    public List<BillingFundDTO> generateBillingFund(List<FeeReportRequest> request, String monthYear) {
        List<BillingFundDTO> billingFundDTOList = new ArrayList<>();

        for (FeeReportRequest feeReportRequest : request) {
            String portfolioCode = feeReportRequest.getPortfolioCode();
            BigDecimal customerFee = feeReportRequest.getCustomerFee();

            List<BillingSKTransaction> skTransactionList = skTransactionService.getAllByPortfolioCode(portfolioCode);

//            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
////                    .parseCaseInsensitive()
////                    .appendPattern("MMM yyyy")
////                    .toFormatter(Locale.ENGLISH);
////
////            TemporalAccessor temporalAccessor = formatter.parse(date);
////            LocalDate parsedDate = LocalDate.from(new MonthYearQuery().queryFrom(temporalAccessor));
////
////            // Set the day of the month to 1 to represent a fixed day
////            LocalDate fixedDate = parsedDate.withDayOfMonth(30);

            LocalDate parsedDate = ConvertDateUtil.getLatestDateOfMonthYear(monthYear);

            int currentMonth = parsedDate.getMonthValue();
            Month currentMonthName = parsedDate.getMonth();
            String monthName = currentMonthName.getDisplayName(TextStyle.SHORT, java.util.Locale.getDefault());

            int currentYear = parsedDate.getYear();

            // Filter transactions for the current month
            List<BillingSKTransaction> filteredTransactions  = skTransactionList.stream()
                    .filter(skTransaction -> skTransaction.getSettlementDate().getYear() == currentYear && skTransaction.getSettlementDate().getMonthValue() == currentMonth)
                    .collect(Collectors.toList());

            BillingFundDTO billingFundDTO = filterTransactionsType(portfolioCode, customerFee,
                    monthName, currentYear,
                    filteredTransactions);

            billingFundDTOList.add(billingFundDTO);
        }
        return billingFundDTOList;
    }

    private BillingFundDTO filterTransactionsType(String portfolioCode, BigDecimal customerFee,
                                                  String currentMonthName, int currentYear,
                                                  List<BillingSKTransaction> transactionList) {
        int transactionCBESTTotal = 0;
        int transactionBIS4Total = 0;

        for (BillingSKTransaction skTransaction : transactionList) {
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

        return calculateBillingFund(portfolioCode, customerFee,
                currentMonthName, currentYear,
                transactionCBESTTotal, transactionBIS4Total);
    }

    private BillingFundDTO calculateBillingFund(String portfolioCode, BigDecimal customerFee,
                                                String currentMonthName, int currentYear,
                                                int transactionCBESTTotal, int transactionBIS4Total) {

        BigDecimal accrualCustodialFee = customerFee
                .divide(BigDecimal.valueOf(1.11), 0, RoundingMode.HALF_UP)  // Divide by 1.11 with 4 decimal places, rounding up
                .setScale(0, RoundingMode.HALF_UP);
        log.info("Accrual Custodial Fee : {}", accrualCustodialFee);

        Integer valueFrequencyS4 = transactionBIS4Total;
        log.info("Value Frequency S4 : {}", valueFrequencyS4);

        BigDecimal s4Fee = new BigDecimal(23_000); // TODO: diambil dari
        log.info("S4 Fee : {}", s4Fee);

        BigDecimal amountDueS4 = new BigDecimal(valueFrequencyS4).multiply(s4Fee).setScale(0, RoundingMode.HALF_UP);
        log.info("Amount Due S4 : {}", amountDueS4);

        BigDecimal totalNominalBeforeTax = accrualCustodialFee.add(amountDueS4).setScale(0, RoundingMode.HALF_UP);
        log.info("Total Nominal Before Tax : {}", totalNominalBeforeTax);

        double taxFee = 0.11; // TODO: diambil dari Fee Parameter
        log.info("Tax Fee : {}", taxFee);

        BigDecimal amountDueTax = totalNominalBeforeTax.multiply(BigDecimal.valueOf(taxFee)).setScale(0, RoundingMode.HALF_UP);
        log.info("Amount Due Tax : {}", amountDueTax);

        Integer valueFrequencyKSEI = transactionCBESTTotal;
        log.info("Value Frequency KSEI : {}", valueFrequencyKSEI);

        BigDecimal kseiFee = new BigDecimal(22_200);
        log.info("KSEI Fee : {}", kseiFee);

        BigDecimal amountDueKSEI = new BigDecimal(valueFrequencyKSEI).multiply(kseiFee).setScale(0, RoundingMode.HALF_UP);
        log.info("Amount Due KSEI : {}", amountDueKSEI);

        BigDecimal totalAmountDue = totalNominalBeforeTax
                .add(amountDueTax)
                .add(amountDueKSEI)
                .setScale(0, RoundingMode.HALF_UP);
        log.info("Total Amount Due : {}", totalAmountDue);

        return BillingFundDTO.builder()
                .portfolioCode(portfolioCode)
                .period(currentMonthName + " " + currentYear)
                .amountDueAccrualCustody(ConvertBigDecimalUtil.formattedBigDecimalToString(accrualCustodialFee))
                .valueFrequencyS4(String.valueOf(valueFrequencyS4))
                .s4Fee(ConvertBigDecimalUtil.formattedBigDecimalToString(s4Fee))
                .amountDueS4(ConvertBigDecimalUtil.formattedBigDecimalToString(amountDueS4))
                .totalNominalBeforeTax(ConvertBigDecimalUtil.formattedBigDecimalToString(totalNominalBeforeTax))
                .taxFee(ConvertBigDecimalUtil.formattedTaxFee(taxFee))
                .amountDueTax(ConvertBigDecimalUtil.formattedBigDecimalToString(amountDueTax))
                .valueFrequencyKSEI(String.valueOf(valueFrequencyKSEI))
                .kseiFee(ConvertBigDecimalUtil.formattedBigDecimalToString(kseiFee))
                .amountDueKSEI(ConvertBigDecimalUtil.formattedBigDecimalToString(amountDueKSEI))
                .totalAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(totalAmountDue))
                .build();
    }

}
