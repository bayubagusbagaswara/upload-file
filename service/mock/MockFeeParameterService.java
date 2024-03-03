package com.services.billingservice.service.mock;

import com.services.billingservice.dto.mock.MockFeeParameterDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MockFeeParameterService {

    String create();

    List<MockFeeParameterDTO> getAll();

    MockFeeParameterDTO getByName(String name);

    BigDecimal getValueByName(String name);

    List<MockFeeParameterDTO> getByNameList(List<String> nameList);

    Map<String, BigDecimal> getValueByNameList(List<String> nameList);


}
