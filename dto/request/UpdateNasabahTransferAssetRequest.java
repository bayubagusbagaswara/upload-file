package com.services.billingservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNasabahTransferAssetRequest {

    private String securityCode;

    private String clientName;

    private double amount;

    private String effectiveDate;

}
