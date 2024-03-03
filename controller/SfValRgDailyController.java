package com.services.billingservice.controller;

import com.opencsv.exceptions.CsvException;
import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.model.BillingSfvalRgDaily;
import com.services.billingservice.service.SfValRgDailyService;
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
@RequestMapping(path = "/api/sfval/rg-daily")
public class SfValRgDailyController {

    @Value("${file.path.sf-val-rg-daily}")
    public String filePath;

    private final SfValRgDailyService rgDailyService;

    public SfValRgDailyController(SfValRgDailyService rgDailyService) {
        this.rgDailyService = rgDailyService;
    }

    @GetMapping(path = "/read-insert")
    public ResponseEntity<ResponseDTO<String>> readAndInsert() throws IOException, CsvException {
//        String filePath = "D:/DST/CSA/FILES/IN/RGDaily_TEST.csv";
        log.info("File Path : {}", filePath);
        String status = rgDailyService.readFileAndInsertToDB(filePath);

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<BillingSfvalRgDaily>>> getAll() {
        List<BillingSfvalRgDaily> sfvalRgDailyList = rgDailyService.getAll();

        ResponseDTO<List<BillingSfvalRgDaily>> response = ResponseDTO.<List<BillingSfvalRgDaily>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfvalRgDailyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/aid")
    public ResponseEntity<ResponseDTO<List<BillingSfvalRgDaily>>> getAllByAid(@RequestParam("aid") String aid) {
        List<BillingSfvalRgDaily> sfvalRgDailyList = rgDailyService.getAllByAid(aid);

        ResponseDTO<List<BillingSfvalRgDaily>> response = ResponseDTO.<List<BillingSfvalRgDaily>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfvalRgDailyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/aid/security-name")
    public ResponseEntity<ResponseDTO<List<BillingSfvalRgDaily>>> getAllByAidAndSecurityName(@RequestParam("aid") String aid,
                                                                                             @RequestParam("securityName") String securityName) {

        List<BillingSfvalRgDaily> sfvalRgDailyList = rgDailyService.getAllByAidAndSecurityName(aid, securityName);

        ResponseDTO<List<BillingSfvalRgDaily>> response = ResponseDTO.<List<BillingSfvalRgDaily>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(sfvalRgDailyList)
                .build();

        return ResponseEntity.ok().body(response);
    }

//    @GetMapping(path = "/aid/date")
//    public ResponseEntity<ResponseDTO<List<BillingSfvalRgDaily>>> getAllByAidAndDate(@RequestParam("aid") String aid,
//                                                                                     @RequestParam("date") String date) {
//
//        List<BillingSfvalRgDaily> sfvalRgDailyList = rgDailyService.getAllByAidAndDate(aid, date);
//
//        ResponseDTO<List<BillingSfvalRgDaily>> response = ResponseDTO.<List<BillingSfvalRgDaily>>builder()
//                .code(HttpStatus.OK.value())
//                .message(HttpStatus.OK.getReasonPhrase())
//                .payload(sfvalRgDailyList)
//                .build();
//
//        return ResponseEntity.ok().body(response);
//    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> deleteAll() {
        String status = rgDailyService.deleteAll();

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(status)
                .build();

        return ResponseEntity.ok().body(response);
    }

}
