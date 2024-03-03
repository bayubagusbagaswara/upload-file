package com.services.billingservice.dto.request.compliance;

import lombok.Data;

@Data
public class ReksadanaRequestDTO {
    private String code;
    private String name;
    private String address;
    private String email;
    private String manajerInvestasi;
    private String pic;
    private String externalCode;
    private String reksadanaType;
    private boolean syariah;
    private boolean conventional;
    private double tnabMinimum;
    private double percentageModal;
    private boolean delete;
}
