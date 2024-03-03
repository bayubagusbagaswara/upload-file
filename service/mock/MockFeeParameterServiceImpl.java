package com.services.billingservice.service.mock;

import com.services.billingservice.dto.mock.MockFeeParameterDTO;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.MockFeeParameter;
import com.services.billingservice.repository.MockFeeParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockFeeParameterServiceImpl implements MockFeeParameterService {

    private final MockFeeParameterRepository mockFeeParameterRepository;

    @Override
    public String create() {
        List<MockFeeParameterDTO> sampleData = createSampleData();
        List<MockFeeParameter> mockFeeParameters = mapToModelList(sampleData);

        mockFeeParameterRepository.saveAll(mockFeeParameters);

        return "Successfully created Mock Fee Parameter";
    }

    @Override
    public List<MockFeeParameterDTO> getAll() {
        List<MockFeeParameter> mockFeeParameterList = mockFeeParameterRepository.findAll();
        return mapToDTOList(mockFeeParameterList);
    }

    @Override
    public MockFeeParameterDTO getByName(String name) {
        MockFeeParameter mockFeeParameter = mockFeeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Mock Fee Parameter not found with name : " + name));
        return mapToDTO(mockFeeParameter);
    }

    @Override
    public BigDecimal getValueByName(String name) {
        MockFeeParameter mockFeeParameter = mockFeeParameterRepository.findByName(name)
                .orElseThrow(() -> new DataNotFoundException("Mock Fee Parameter not found with name : " + name));
        return mockFeeParameter.getValue();
    }

    @Override
    public List<MockFeeParameterDTO> getByNameList(List<String> nameList) {
        List<MockFeeParameter> feeParameterList = mockFeeParameterRepository.findMockFeeParameterByNameList(nameList);
        return mapToDTOList(feeParameterList);
    }

    @Override
    public Map<String, BigDecimal> getValueByNameList(List<String> nameList) {
        Map<String, BigDecimal> dataMap = new HashMap<>();

        List<MockFeeParameter> feeParameterList = mockFeeParameterRepository.findMockFeeParameterByNameList(nameList);

        for (String name : nameList) {
            for (MockFeeParameter feeParameter : feeParameterList) {
                if (feeParameter.getName().equals(name)) {
                    dataMap.put(feeParameter.getName(), feeParameter.getValue());
                    break; // Optional: Exit the inner loop if a match is found
                }
            }
        }

        return dataMap;
    }

    private static MockFeeParameter mapToModel(MockFeeParameterDTO dto) {
        return MockFeeParameter.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .value(new BigDecimal(dto.getValue()))
                .build();
    }

    private static List<MockFeeParameter> mapToModelList(List<MockFeeParameterDTO> dtoList) {
        return dtoList.stream()
                .map(MockFeeParameterServiceImpl::mapToModel)
                .collect(Collectors.toList());
    }

    private static MockFeeParameterDTO mapToDTO(MockFeeParameter mockFeeParameter) {
        return MockFeeParameterDTO.builder()
                .id(String.valueOf(mockFeeParameter.getId()))
                .name(mockFeeParameter.getName())
                .description(mockFeeParameter.getDescription())
                .value(String.valueOf(mockFeeParameter.getValue()))
                .build();
    }

    private static List<MockFeeParameterDTO> mapToDTOList(List<MockFeeParameter> mockFeeParameterList) {
        return mockFeeParameterList.stream()
                .map(MockFeeParameterServiceImpl::mapToDTO)
                .collect(Collectors.toList());
    }


    private static List<MockFeeParameterDTO> createSampleData() {
        List<MockFeeParameterDTO> list = new ArrayList<>();

        MockFeeParameterDTO mockFeeParameterDTO = MockFeeParameterDTO.builder()
                .name("TRANSACTION_HANDLING_IDR")
                .description("Transaction Handling Fee IDR")
                .value("50000")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO1 = MockFeeParameterDTO.builder()
                .name("KSEI")
                .description("KSEI Fee")
                .value("22200")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO2 = MockFeeParameterDTO.builder()
                .name("BIS4")
                .description("BI-SSSS Fee")
                .value("23000")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO3 = MockFeeParameterDTO.builder()
                .name("VAT")
                .description("PPN (VAT) Fee")
                .value("0.11")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO5 = MockFeeParameterDTO.builder()
                .name("ADMINISTRATION_SET_UP")
                .description("Administration Set Up Fee USD")
                .value("5000")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO6 = MockFeeParameterDTO.builder()
                .name("SIGNING_REPRESENTATION")
                .description("Signing Representation Fee USD")
                .value("2000")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO7 = MockFeeParameterDTO.builder()
                .name("SECURITY_AGENT")
                .description("Security Agent Fee USD")
                .value("10000")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO8 = MockFeeParameterDTO.builder()
                .name("TRANSACTION_HANDLING_USD")
                .description("Transaction Handling Fee USD")
                .value("100")
                .build();

        MockFeeParameterDTO mockFeeParameterDTO9 = MockFeeParameterDTO.builder()
                .name("OTHER")
                .description("Other Fee USD")
                .value("5000")
                .build();

        list.add(mockFeeParameterDTO);
        list.add(mockFeeParameterDTO1);
        list.add(mockFeeParameterDTO2);
        list.add(mockFeeParameterDTO3);
        list.add(mockFeeParameterDTO5);
        list.add(mockFeeParameterDTO6);
        list.add(mockFeeParameterDTO7);
        list.add(mockFeeParameterDTO8);
        list.add(mockFeeParameterDTO9);

        return list;
    }

}
