package com.services.billingservice.service;

import com.services.billingservice.dto.request.CreateBillingCustomerRequest;
import com.services.billingservice.dto.response.BillingCustomerDTO;

import java.util.List;

public interface BillingCustomerService {

    BillingCustomerDTO create(CreateBillingCustomerRequest request);
    List<BillingCustomerDTO> upload(List<CreateBillingCustomerRequest> request);
}
