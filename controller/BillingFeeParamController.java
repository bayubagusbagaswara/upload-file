package com.services.billingservice.controller;

import com.services.billingservice.dto.BillingFeeParamDTO;
import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.request.BillingFeeParamRequest;
import com.services.billingservice.service.BillingFeeParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping(path = "/feeParam")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BillingFeeParamController {

    private final BillingFeeParamService billingFeeParamService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>>
    create(@RequestBody BillingFeeParamRequest request) {

        BillingFeeParamDTO billingFeeParamDTO = billingFeeParamService.create(request);

        ResponseDTO<String> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload("Successfully create Fee Param with id :" + billingFeeParamDTO.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping(path = "/upload")
    public ResponseEntity<ResponseDTO<List<BillingFeeParamDTO>>> upload(
            @RequestBody List<BillingFeeParamRequest> billingFeeParamList) {
        List<BillingFeeParamDTO> billingFeeParamDTO = billingFeeParamService.upload(billingFeeParamList);

        ResponseDTO<List<BillingFeeParamDTO>> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setPayload(billingFeeParamDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping(path = "/{code}")
    public ResponseEntity<ResponseDTO<BillingFeeParamDTO>> updateByCode(@PathVariable("code") String code,
                                                                           @RequestBody BillingFeeParamRequest request) {
        BillingFeeParamDTO billingFeeParamDTO = billingFeeParamService.updateByCode(code, request);
        ResponseDTO<BillingFeeParamDTO> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(billingFeeParamDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/update")
    public ResponseEntity<ResponseDTO<List<BillingFeeParamDTO>>> update(
            @RequestBody List<BillingFeeParamRequest> billingFeeParamList) {
        List<BillingFeeParamDTO> billingFeeParamDTO = billingFeeParamService.updateUploadByCode(billingFeeParamList );

        ResponseDTO<List<BillingFeeParamDTO>> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setPayload(billingFeeParamDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
