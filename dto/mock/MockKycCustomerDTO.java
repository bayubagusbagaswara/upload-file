package com.services.billingservice.dto.mock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockKycCustomerDTO {

    private String id;

    private String aid;

    private String kseiSafeCode;

    private String minimumFee;

    private String customerFee;

    private String journal;

    private String billingCategory;

    private String billingType;

    private String billingTemplate;

}
