package com.services.billingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NasabahTransferAssetDTO {

    private String id;

    private String securityCode;

    private String clientName;

    private double amount;

    private String effectiveDate;

    private LocalDateTime createdAt;

    private boolean isDeleted;

}
