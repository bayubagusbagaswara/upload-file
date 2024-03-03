package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.service.mock.MockKycCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/mock-kyc-customer")
@RequiredArgsConstructor
public class MockKycCustomerController {

    private final MockKycCustomerService mockKycCustomerService;

    @GetMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>> create() {
        log.info("Start Create Mock Kyc Customer");

        String status = mockKycCustomerService.create();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<MockKycCustomerDTO>>> getAll() {

        List<MockKycCustomerDTO> mockKycCustomerDTOList = mockKycCustomerService.getAll();

        ResponseDTO<List<MockKycCustomerDTO>> response = ResponseDTO.<List<MockKycCustomerDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(mockKycCustomerDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDTO<String>> delete() {
        String status = mockKycCustomerService.deleteAll();
        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
