package com.services.billingservice.controller;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.model.BillingSKTransaction;
import com.services.billingservice.service.SkTranService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/sk-tran")
public class SkTranController {

    @Value("${file.path.sk-tran}")
    private String filePath;

    private final SkTranService skTranService;

    public SkTranController(SkTranService skTranService) {
        this.skTranService = skTranService;
    }

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() throws IOException, CsvException {
        log.info("File Path : {}", filePath);

        String status = skTranService.readFileAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingSKTransaction>>> getAll() {
        List<BillingSKTransaction> skTransactionList = skTranService.getAll();

        ResponseDTO<List<BillingSKTransaction>> response = ResponseDTO.<List<BillingSKTransaction>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(skTransactionList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/portfolio-code")
    public ResponseEntity<ResponseDTO<List<BillingSKTransaction>>> getAllByPortfolioCode(@RequestParam("portfolioCode") String portfolioCode) {
        List<BillingSKTransaction> skTransactionList = skTranService.getAllByPortfolioCode(portfolioCode);

        ResponseDTO<List<BillingSKTransaction>> response = ResponseDTO.<List<BillingSKTransaction>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(skTransactionList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/portfolio-code/system")
    public ResponseEntity<ResponseDTO<List<BillingSKTransaction>>> getAllByPortfolioCodeAndSystem(
            @RequestParam("portfolioCode") String portfolioCode,
            @RequestParam("system") String system) {

        List<BillingSKTransaction> skTransactionList = skTranService.getAllByPortfolioCodeAndSystem(portfolioCode, system);

        ResponseDTO<List<BillingSKTransaction>> response = ResponseDTO.<List<BillingSKTransaction>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(skTransactionList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = skTranService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
