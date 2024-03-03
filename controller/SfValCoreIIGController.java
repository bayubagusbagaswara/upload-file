package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.request.CreateSfValCoreIIGRequest;
import com.services.billingservice.service.SfValCoreIIGService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/sf-val/iig")
public class SfValCoreIIGController {

    private final SfValCoreIIGService sfValCoreIIGService;

    public SfValCoreIIGController(SfValCoreIIGService sfValCoreIIGService) {
        this.sfValCoreIIGService = sfValCoreIIGService;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>> create(@RequestBody CreateSfValCoreIIGRequest request) {

        String status = sfValCoreIIGService.create(request);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
