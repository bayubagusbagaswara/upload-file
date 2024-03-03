package com.services.billingservice.service.mock;

import com.services.billingservice.dto.mock.MockKycCustomerDTO;

import java.util.List;

public interface MockKycCustomerService {

    String create();

    List<MockKycCustomerDTO> getAll();

    List<MockKycCustomerDTO> getByAid(String aid);

    List<MockKycCustomerDTO> getAllByBillingCategoryAndBillingType(
            String billingCategory,
            String billingType
    );

    String deleteAll();

}
