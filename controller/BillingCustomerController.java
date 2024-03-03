package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.request.CreateBillingCustomerRequest;
import com.services.billingservice.dto.response.BillingCustomerDTO;
import com.services.billingservice.service.BillingCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/customer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BillingCustomerController {

    private final BillingCustomerService billingCustomerService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>> create(@RequestBody CreateBillingCustomerRequest request) {

        BillingCustomerDTO billingCustomerDTO = billingCustomerService.create(request);

        ResponseDTO<String> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload("Successfully created nasabah transfer asset with id : " + billingCustomerDTO.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<ResponseDTO<List<BillingCustomerDTO>>> upload(
            @RequestBody List<CreateBillingCustomerRequest> billingCustomerList) {

        List<BillingCustomerDTO> billingCustomerDTO = billingCustomerService.upload(billingCustomerList);

        ResponseDTO<List<BillingCustomerDTO>> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setPayload(billingCustomerDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
