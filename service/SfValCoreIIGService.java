package com.services.billingservice.service;

import com.services.billingservice.dto.request.CreateSfValCoreIIGRequest;
import com.services.billingservice.model.BillingSfvalCoreIIG;

import java.util.List;

public interface SfValCoreIIGService {

    String create(CreateSfValCoreIIGRequest request);

    List<BillingSfvalCoreIIG> getAll();

    List<BillingSfvalCoreIIG> getAllByCustomerCode(String customerCode);

    List<BillingSfvalCoreIIG> getAllByAidAndMonthYear(String aid, String monthYear);

    List<BillingSfvalCoreIIG> getAllByAidLimit(String aid, int limit);
}
