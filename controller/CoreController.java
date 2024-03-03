package com.services.billingservice.controller;

import com.services.billingservice.constant.BillingCategoryConstant;
import com.services.billingservice.constant.BillingTypeConstant;
import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.service.Core1Service;
import com.services.billingservice.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/billing/core")
public class CoreController {

    private final Core1Service core1Service;

    public CoreController(Core1Service core1Service) {
        this.core1Service = core1Service;
    }

    @GetMapping(path = "/generate")
    public ResponseEntity<ResponseDTO<String>> generate(
        @RequestParam("category") String category,
        @RequestParam("type") String type,
        @RequestParam("monthYear") String monthYear
    ) {

        String categoryUpperCase = category.toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(type);
        log.info("Start generate Category : {}, Type : {}, Month Year : {}", categoryUpperCase,typeUpperCase, monthYear);


        ResponseDTO<String> response = new ResponseDTO<>();

        String status = "";

        if (BillingCategoryConstant.CORE_CATEGORY.equalsIgnoreCase(categoryUpperCase)) {

            if (BillingTypeConstant.TYPE_1.equalsIgnoreCase(typeUpperCase)) {
                status = core1Service.calculate1(categoryUpperCase, typeUpperCase, monthYear);

            } else if (BillingTypeConstant.TYPE_2.equalsIgnoreCase(typeUpperCase)) {
                status = "Success create billing Core Type 2";
            } else {
                status = "Type is not Billing Core";
            }

        } else {
            status = "Category is not Billing Core";
        }

        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setPayload(status);

        return ResponseEntity.ok().body(response);
    }

}
