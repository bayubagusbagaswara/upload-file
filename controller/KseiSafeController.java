package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.model.BillingKseiSafe;
import com.services.billingservice.service.KseiSafeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/ksei-safe")
public class KseiSafeController {

    @Value("${file.path.ksei-safe}")
    private String filePath;

    private final KseiSafeService kseiSafeService;

    public KseiSafeController(KseiSafeService kseiSafeService) {
        this.kseiSafeService = kseiSafeService;
    }

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() {

        String status = kseiSafeService.readAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingKseiSafe>>> getAll() {

        List<BillingKseiSafe> kseiSafeList = kseiSafeService.getAll();

        ResponseDTO<List<BillingKseiSafe>> response = ResponseDTO.<List<BillingKseiSafe>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kseiSafeList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/code")
    public ResponseEntity<ResponseDTO<BillingKseiSafe>> getByFeeAccount(
            @RequestParam("kseiSafeCode") String kseiSafeCode) {

        BillingKseiSafe kseiSafe = kseiSafeService.getByKseiSafeCode(kseiSafeCode);

        ResponseDTO<BillingKseiSafe> response = ResponseDTO.<BillingKseiSafe>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(kseiSafe)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(path = "/delete")
    public ResponseEntity<ResponseDTO<String>> delete() {

        String status = kseiSafeService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);

    }
}
