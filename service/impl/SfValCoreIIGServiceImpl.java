package com.services.billingservice.service.impl;

import com.services.billingservice.dto.request.CreateSfValCoreIIGRequest;
import com.services.billingservice.model.BillingSfvalCoreIIG;
import com.services.billingservice.repository.SfValCoreIIGRepository;
import com.services.billingservice.service.SfValCoreIIGService;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SfValCoreIIGServiceImpl implements SfValCoreIIGService {

    private final SfValCoreIIGRepository sfValCoreIIGRepository;

    public SfValCoreIIGServiceImpl(SfValCoreIIGRepository sfValCoreIIGRepository) {
        this.sfValCoreIIGRepository = sfValCoreIIGRepository;
    }

    @Override
    public String create(CreateSfValCoreIIGRequest request) {
        String customerCode = request.getCustomerCode();
        String customerName = request.getCustomerName();
        BigDecimal totalHolding = new BigDecimal(request.getTotalHolding());
        Integer priceTrub = request.getPriceTrub();

        Double safeFee = 0.0005;

        BigDecimal totalMarketValue = calculateTotalMarketValue(totalHolding, priceTrub);
        BigDecimal safekeepingFee = calculateSafekeepingFee(totalMarketValue, safeFee);

        log.info("Customer Code : {}", customerCode);
        log.info("Customer Name : {}", customerName);
        log.info("Total Holding : {}", totalHolding);

        List<BillingSfvalCoreIIG> sfvalCoreIIGList = new ArrayList<>();

        for (int i = 1; i <= 31; i++) {
            BillingSfvalCoreIIG sfvalCoreIIG = BillingSfvalCoreIIG.builder()
                    .customerCode(customerCode)
                    .customerName(customerName)
                    .date(i)
                    .totalHolding(totalHolding)
                    .priceTRUB(priceTrub)
                    .totalMarketValue(totalMarketValue)
                    .safekeepingFee(safekeepingFee)
                    .build();

            sfvalCoreIIGList.add(sfvalCoreIIG);
        }

        // save all to the database
        sfValCoreIIGRepository.saveAll(sfvalCoreIIGList);

        return "Successfully created sfval core IIG";
    }

    @Override
    public List<BillingSfvalCoreIIG> getAll() {
        return sfValCoreIIGRepository.findAll();
    }

    @Override
    public List<BillingSfvalCoreIIG> getAllByCustomerCode(String customerCode) {
        log.info("Start get all by customer code : {}", customerCode);
        return sfValCoreIIGRepository.findAllByCustomerCodeOrderByDateAsc(customerCode);
    }

    @Override
    public List<BillingSfvalCoreIIG> getAllByAidAndMonthYear(String aid, String monthYear) {
        List<BillingSfvalCoreIIG> sfvalCoreIIGList = sfValCoreIIGRepository.findAllByCustomerCodeOrderByDateAsc(aid);

        LocalDate lastDate = ConvertDateUtil.getLatestDateOfMonthYear(monthYear);
        log.info("Last Date : {}", lastDate);

        int dayOfMonth = lastDate.getDayOfMonth();
        log.info("Day of Month : {}", dayOfMonth);

        List<BillingSfvalCoreIIG> takenData = sfvalCoreIIGList.stream()
                .limit(dayOfMonth)
                .collect(Collectors.toList());

        log.info("Taken Data Size : {}", takenData);

        return takenData;
    }

    @Override
    public List<BillingSfvalCoreIIG> getAllByAidLimit(String aid, int limit) {
        List<BillingSfvalCoreIIG> sfvalCoreIIGList = sfValCoreIIGRepository.findAllByCustomerCodeOrderByDateAsc(aid);

        List<BillingSfvalCoreIIG> takenData = sfvalCoreIIGList.stream()
                .limit(limit)
                .collect(Collectors.toList());

        log.info("Taken Data Size : {}", takenData);

        return takenData;
    }

    private static BigDecimal calculateTotalMarketValue(BigDecimal totalHolding, Integer priceTrub) {
        return totalHolding
                .multiply(new BigDecimal(priceTrub))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateSafekeepingFee(BigDecimal totalMarketValue, Double safeFee) {
        return totalMarketValue
                .multiply(new BigDecimal(safeFee))
                .divide(new BigDecimal(365), 2, RoundingMode.HALF_UP);
    }

}
