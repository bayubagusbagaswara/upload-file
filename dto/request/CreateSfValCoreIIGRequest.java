package com.services.billingservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSfValCoreIIGRequest {

    private String customerCode;

    private String customerName; // Alam Manunggal, Indo Infrastruktur, Mandala Kapital

    private String totalHolding;

    private Integer priceTrub;

}
