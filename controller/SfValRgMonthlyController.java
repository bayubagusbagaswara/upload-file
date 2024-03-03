package com.services.billingservice.controller;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.model.BillingSfvalRgMonthly;
import com.services.billingservice.service.SfValRgMonthlyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RestController
@RequestMapping(path = "/api/sfval-rg-monthly")
public class SfValRgMonthlyController {

    @Value("${file.path.bill-monthly}")
    private String filePath;

    private final SfValRgMonthlyService sfValRgMonthlyService;

    public SfValRgMonthlyController(SfValRgMonthlyService sfValRgMonthlyService) {
        this.sfValRgMonthlyService = sfValRgMonthlyService;
    }

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() throws IOException, CsvException {
        log.info("File Path : {}", filePath);

        String status = sfValRgMonthlyService.readFileAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingSfvalRgMonthly>>> getAll() {
        List<BillingSfvalRgMonthly> sfvalRgMonthlyList = sfValRgMonthlyService.getAll();

        ResponseDTO<List<BillingSfvalRgMonthly>> response = ResponseDTO.<List<BillingSfvalRgMonthly>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfvalRgMonthlyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/aid")
    public ResponseEntity<ResponseDTO<List<BillingSfvalRgMonthly>>> getAllByAid(@RequestParam("aid") String aid) {
        List<BillingSfvalRgMonthly> sfvalRgMonthlyList = sfValRgMonthlyService.getAllByAid(aid);

        ResponseDTO<List<BillingSfvalRgMonthly>> response = ResponseDTO.<List<BillingSfvalRgMonthly>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfvalRgMonthlyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

//    @GetMapping(path = "/aid/security-name")
//    public ResponseEntity<ResponseDTO<List<BillingSfvalRgMonthly>>> getAllByAidAndSecurityName(
//            @RequestParam("aid") String aid,
//            @RequestParam("securityName") String securityName) {
//
//        List<BillingSfvalRgMonthly> sfvalRgMonthlyList = sfValRgMonthlyService.getAllByAidAndSecurityName(aid, securityName);
//
//        ResponseDTO<List<BillingSfvalRgMonthly>> response = ResponseDTO.<List<BillingSfvalRgMonthly>>builder()
//                .code(HttpStatus.OK.value())
//                .message(HttpStatus.OK.getReasonPhrase())
//                .payload(sfvalRgMonthlyList)
//                .build();
//
//        return ResponseEntity.ok().body(response);
//    }

}
