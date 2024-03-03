package com.services.billingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNasabahTransferAssetResponse {

    private String id;

    private String customerCode;

    private String name;

    private String period;

}
