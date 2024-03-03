package com.services.billingservice.service;

import com.services.billingservice.dto.fund.BillingFundDTO;
import com.services.billingservice.dto.fund.FeeReportRequest;

import java.util.List;

public interface BillingFundService {

    List<BillingFundDTO> generateBillingFund(List<FeeReportRequest> request, String date);
}
