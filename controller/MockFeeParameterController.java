package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.mock.MockFeeParameterDTO;
import com.services.billingservice.service.mock.MockFeeParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/mock-fee-parameter")
@RequiredArgsConstructor
public class MockFeeParameterController {

    private final MockFeeParameterService mockFeeParameterService;

    @GetMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>> create() {
        String status = mockFeeParameterService.create();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<MockFeeParameterDTO>>> getAll() {
        List<MockFeeParameterDTO> feeParameterDTOList = mockFeeParameterService.getAll();

        ResponseDTO<List<MockFeeParameterDTO>> response = ResponseDTO.<List<MockFeeParameterDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(feeParameterDTOList)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
