package com.services.billingservice.service.mock;

import com.services.billingservice.dto.mock.MockExchangeRateDTO;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.MockExchangeRate;
import com.services.billingservice.repository.MockExchangeRateRepository;
import com.services.billingservice.utils.ConvertDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockExchangeRateServiceImpl implements MockExchangeRateService {

    private final MockExchangeRateRepository exchangeRateRepository;

    @Override
    public String create() {
        List<MockExchangeRateDTO> dtoList = insertData();
        List<MockExchangeRate> mockExchangeRateList = mapToModelList(dtoList);

        exchangeRateRepository.saveAll(mockExchangeRateList);

        return "Successfully insert data exchange rate";
    }

    @Override
    public MockExchangeRateDTO getByCurrencyAndDate(String currency, String monthYear) {
        String currencyUppercase = currency.toUpperCase();

        LocalDate latestDateOfMonthYear = ConvertDateUtil.getLatestDateOfMonthYear(monthYear);

        log.info("Latest Date of Month and Year : {}", latestDateOfMonthYear);

        MockExchangeRate mockExchangeRate = exchangeRateRepository.findByCurrencyAndDate(currencyUppercase, latestDateOfMonthYear)
                .orElseThrow(() -> new DataNotFoundException("Exchange Rate not found with month and year : " + monthYear));

        return mapToDTO(mockExchangeRate);
    }

    @Override
    public List<MockExchangeRateDTO> getAll() {
        List<MockExchangeRate> mockExchangeRateList = exchangeRateRepository.findAll();
        return mapToDTOList(mockExchangeRateList);
    }

    private static MockExchangeRateDTO mapToDTO(MockExchangeRate mockExchangeRate) {
        return MockExchangeRateDTO.builder()
                .id(String.valueOf(mockExchangeRate.getId()))
                .date(String.valueOf(mockExchangeRate.getDate()))
                .currency(String.valueOf(mockExchangeRate.getCurrency()))
                .value(String.valueOf(mockExchangeRate.getValue()))
                .build();
    }

    private static List<MockExchangeRateDTO> mapToDTOList(List<MockExchangeRate> mockExchangeRateList) {
        return mockExchangeRateList.stream()
                .map(MockExchangeRateServiceImpl::mapToDTO)
                .collect(Collectors.toList());
    }

    private static MockExchangeRate mapToModel(MockExchangeRateDTO mockExchangeRateDTO) {
        return MockExchangeRate.builder()
                .date(LocalDate.parse(mockExchangeRateDTO.getDate()))
                .currency(mockExchangeRateDTO.getCurrency())
                .value(new BigDecimal(mockExchangeRateDTO.getValue()))
                .build();
    }

    private static List<MockExchangeRate> mapToModelList(List<MockExchangeRateDTO> mockExchangeRateDTOList) {
        return mockExchangeRateDTOList.stream()
                .map(MockExchangeRateServiceImpl::mapToModel)
                .collect(Collectors.toList());
    }

    private static List<MockExchangeRateDTO> insertData() {
        List<MockExchangeRateDTO> list = new ArrayList<>();

        MockExchangeRateDTO data1 = MockExchangeRateDTO.builder()
                .date("2023-11-30")
                .currency("USD")
                .value("15000")
                .build();

        MockExchangeRateDTO data2 = MockExchangeRateDTO.builder()
                .date("2023-12-31")
                .currency("USD")
                .value("15200")
                .build();

        list.add(data1);
        list.add(data2);

        return list;
    }

}
