package com.services.billingservice.dto;

import com.services.billingservice.enums.ChangeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataChangeResponseDto {

    private Long idDataChange;

    private ChangeAction action;

    private String entityId;

    private String inputDate;

    private String inputerId;

    private String entityClassName;

    private Object dataBefore;

    private Object dataAfter;
}
