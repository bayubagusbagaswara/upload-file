package com.services.billingservice.service.impl;

import com.services.billingservice.constant.CurrencyConstant;
import com.services.billingservice.constant.FeeParameterNameConstant;
import com.services.billingservice.dto.core.CoreType8DTO;
import com.services.billingservice.dto.mock.MockExchangeRateDTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.BillingSfvalCoreIIG;
import com.services.billingservice.service.Core8Service;
import com.services.billingservice.service.mock.MockExchangeRateService;
import com.services.billingservice.service.mock.MockFeeParameterService;
import com.services.billingservice.service.mock.MockKycCustomerService;
import com.services.billingservice.service.SfValCoreIIGService;
import com.services.billingservice.utils.ConvertBigDecimalUtil;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class Core8ServiceImpl implements Core8Service {

    private final MockKycCustomerService mockKycCustomerService;
    private final MockFeeParameterService feeParameterService;
    private final MockExchangeRateService exchangeRateService;
    private final SfValCoreIIGService sfValCoreIIGService;

    @Override
    public List<CoreType8DTO> calculate(String category, String type, String monthYear) {

        // TODO: Retrieve Kyc Customer data via KycCustomerService
        List<MockKycCustomerDTO> kycCustomerDTOList = mockKycCustomerService.getAllByBillingCategoryAndBillingType(category, type);

        // TODO: Retrieve Exchange Rate
        MockExchangeRateDTO exchangeRateDTO = exchangeRateService.getByCurrencyAndDate(CurrencyConstant.CURRENCY_USD, monthYear);
        BigDecimal exchangeRate = new BigDecimal(exchangeRateDTO.getValue());

        List<String> feeParamNameList = new ArrayList<>();
        feeParamNameList.add(FeeParameterNameConstant.TRANSACTION_HANDLING_USD);
        feeParamNameList.add(FeeParameterNameConstant.ADMINISTRATION_SET_UP);
        feeParamNameList.add(FeeParameterNameConstant.SIGNING_REPRESENTATION);
        feeParamNameList.add(FeeParameterNameConstant.SECURITY_AGENT);
        feeParamNameList.add(FeeParameterNameConstant.OTHER);
        feeParamNameList.add(FeeParameterNameConstant.VAT);

        Map<String, BigDecimal> feeParameterMap = feeParameterService.getValueByNameList(feeParamNameList);

        // TODO: get value fee from feeParameterMapList
        BigDecimal administrationSetUpFee = feeParameterMap.get(FeeParameterNameConstant.ADMINISTRATION_SET_UP);
        BigDecimal transactionHandlingFee = feeParameterMap.get(FeeParameterNameConstant.TRANSACTION_HANDLING_USD);
        BigDecimal signingRepresentationFee = feeParameterMap.get(FeeParameterNameConstant.SIGNING_REPRESENTATION);
        BigDecimal securityAgentFee = feeParameterMap.get(FeeParameterNameConstant.SECURITY_AGENT);
        BigDecimal otherFee = feeParameterMap.get(FeeParameterNameConstant.OTHER);
        BigDecimal vatFee = feeParameterMap.get(FeeParameterNameConstant.VAT);
        String vatFeeStr = ConvertBigDecimalUtil.formattedTaxFeeBigDecimal(vatFee);

        // TODO: Initialization variable
        int administrationSetUpItem;
        BigDecimal administrationSetUpAmountDue;
        int signingRepresentationItem;
        BigDecimal signingRepresentationAmountDue;
        int securityAgentItem;
        BigDecimal securityAgentAmountDue;
        int transactionHandlingItem;
        BigDecimal transactionHandlingAmountDue;
        int safekeepingItem;
        BigDecimal safekeepingAmountDueUSD;
        int otherItem = 0;
        BigDecimal otherAmountDue;
        BigDecimal totalAmountDueBeforeTax;
        BigDecimal vatAmountDueUSD;
        BigDecimal totalAmountDueAfterTax;
        List<CoreType8DTO> coreType8DTOList = new ArrayList<>();

        // TODO: Retrieve day of month for limit data
        LocalDate lastDate = ConvertDateUtil.getLatestDateOfMonthYear(monthYear);
        int dayOfMonth = lastDate.getDayOfMonth();
        log.info("Last Date : {} and Day of Month : {}", lastDate, dayOfMonth);

        // TODO: Process calculate Billing
        for (int i = 1; i <= kycCustomerDTOList.size(); i++) {

            // TODO: Kyc Customer
            MockKycCustomerDTO kycCustomerDTO = kycCustomerDTOList.get(i);
            String aid = kycCustomerDTO.getAid();
            String customerFee = kycCustomerDTO.getCustomerFee();

            // TODO: SfVal Core IIG, harus sudah sesuai limit
            List<BillingSfvalCoreIIG> sfValCoreIIGList = sfValCoreIIGService.getAllByAidAndMonthYear(aid, monthYear);

            // TODO: Safekeeping Fee IDR
            BigDecimal safekeepingFeeIDR = calculateTotalSafekeepingIDR(aid, sfValCoreIIGList);

            // TODO: Safekeeping Amount Due
            safekeepingAmountDueUSD = calculateTotalSafekeepingUSD(safekeepingFeeIDR, exchangeRate);

            // TODO: Administration Set Up Item
            administrationSetUpItem = getAdministrationSetUpItem();

            // TODO: Administration Set Up Amount Due
            administrationSetUpAmountDue = getAdministrationSetAmountDue();

            signingRepresentationItem = getSigningRepresentationItem();

            signingRepresentationAmountDue = getSigningRepresentationAmountDue();

            securityAgentItem = getSecurityAgentItem();

            securityAgentAmountDue = getSecurityAgentAmountDue();

            transactionHandlingItem = getTransactionHandlingItem();

            transactionHandlingAmountDue = getTransactionHandlingAmountDue();

            safekeepingItem = getSafekeepingItem();

            otherAmountDue = getOtherAmountDue();

            // TODO: Total Amount Due Before VAT (1-6)
            totalAmountDueBeforeTax = calculateTotalAmountDueBeforeTax(
                    administrationSetUpAmountDue, signingRepresentationAmountDue,
                    securityAgentAmountDue, transactionHandlingAmountDue,
                    safekeepingAmountDueUSD, otherAmountDue
            );

            // TODO: PPN
            vatAmountDueUSD = calculateAmountDueUSD(vatFee, safekeepingAmountDueUSD);

            // TODO: Total Amount Due After VAT
            totalAmountDueAfterTax = calculateTotalAmountDueAfterTax(vatAmountDueUSD, safekeepingAmountDueUSD);

            CoreType8DTO coreType8DTO = CoreType8DTO.builder()
                    .administrationSetUpItem(String.valueOf(administrationSetUpItem))
                    .administrationSetUpFee(ConvertBigDecimalUtil.formattedBigDecimalToString(administrationSetUpFee))
                    .administrationAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(administrationSetUpAmountDue))

                    .signingRepresentationItem(String.valueOf(signingRepresentationItem))
                    .signingRepresentationFee(ConvertBigDecimalUtil.formattedBigDecimalToString(signingRepresentationFee))
                    .signingRepresentationAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(signingRepresentationAmountDue))

                    .securityAgentItem(String.valueOf(securityAgentItem))
                    .securityAgentFee(ConvertBigDecimalUtil.formattedBigDecimalToString(securityAgentFee))
                    .securityAgentAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(securityAgentAmountDue))

                    .transactionHandlingItem(String.valueOf(transactionHandlingItem))
                    .transactionHandlingFee(ConvertBigDecimalUtil.formattedBigDecimalToString(transactionHandlingFee))
                    .transactionHandlingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(transactionHandlingAmountDue))

                    .safekeepingItem(String.valueOf(safekeepingItem))
                    .safekeepingFee(customerFee)
                    .safekeepingAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(safekeepingAmountDueUSD))

                    .otherItem(String.valueOf(otherItem))
                    .otherFee(ConvertBigDecimalUtil.formattedBigDecimalToString(otherFee))
                    .otherAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(otherAmountDue))

                    .totalAmountDueBeforeTax(ConvertBigDecimalUtil.formattedBigDecimalToString(totalAmountDueBeforeTax))

                    .taxFee(vatFeeStr)
                    .taxAmountDue(ConvertBigDecimalUtil.formattedBigDecimalToString(vatAmountDueUSD))

                    .totalAmountDueAfterTax(ConvertBigDecimalUtil.formattedBigDecimalToString(totalAmountDueAfterTax))

                    .build();

            coreType8DTOList.add(coreType8DTO);
        }

        return coreType8DTOList;
    }

    private static BigDecimal getOtherAmountDue() {
        return BigDecimal.ZERO;
    }

    private static int getSafekeepingItem() {
        return 0;
    }

    private static BigDecimal getTransactionHandlingAmountDue() {
        return BigDecimal.ZERO;
    }

    private static int getTransactionHandlingItem() {
        return 0;
    }

    private static BigDecimal getSecurityAgentAmountDue() {
        return BigDecimal.ZERO;
    }

    private static int getSecurityAgentItem() {
        return 0;
    }

    private static BigDecimal calculateTotalAmountDueAfterTax(BigDecimal vatAmountDueUSD, BigDecimal safekeepingAmountDueUSD) {
        return safekeepingAmountDueUSD
                .add(vatAmountDueUSD);
    }

    private static BigDecimal calculateAmountDueUSD(BigDecimal vatFee, BigDecimal safekeepingAmountDueUSD) {
        return safekeepingAmountDueUSD
                .multiply(vatFee)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalSafekeepingUSD(BigDecimal safekeepingFeeIDR, BigDecimal exchangeRate) {
        return safekeepingFeeIDR
                .divide(exchangeRate, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalSafekeepingIDR(String aid, List<BillingSfvalCoreIIG> sfValCoreIIGList) {
        return sfValCoreIIGList.stream()
                .map(BillingSfvalCoreIIG::getSafekeepingFee)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTotalAmountDueBeforeTax(BigDecimal administrationSetUpAmountDue, BigDecimal signingRepresentationAmountDue, BigDecimal securityAgentAmountDue, BigDecimal transactionHandlingAmountDue, BigDecimal safekeepingAmountDue, BigDecimal otherAmountDue) {
        return administrationSetUpAmountDue
                .add(signingRepresentationAmountDue)
                .add(securityAgentAmountDue)
                .add(transactionHandlingAmountDue)
                .add(safekeepingAmountDue)
                .add(otherAmountDue)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static int getAdministrationSetUpItem() {
        return 0;
    }

    private static BigDecimal getAdministrationSetAmountDue() {
        return BigDecimal.ZERO;
    }

    private static int getSigningRepresentationItem() {
        return 0;
    }

    private static BigDecimal getSigningRepresentationAmountDue() {
        return BigDecimal.ZERO;
    }


}
