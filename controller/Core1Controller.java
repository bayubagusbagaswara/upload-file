package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.core.CoreType1DTO;
import com.services.billingservice.service.Core1Service;
import com.services.billingservice.utils.ConvertDateUtil;
import com.services.billingservice.utils.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/core-1")
@RequiredArgsConstructor
public class Core1Controller {

    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;
    private final Core1Service core1Service;

    @GetMapping(path = "/calculate")
    public ResponseEntity<ResponseDTO<String>> calculate(
            @RequestParam("category") String category,
            @RequestParam("type") String type,
            @RequestParam("monthYear") String monthYear) {

        List<CoreType1DTO> calculate = core1Service.calculate(category, type, monthYear);

        // save result calculate to database

        ResponseDTO<String> response = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload("Success calculate billing core type 1")
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> generatePdf(
            @RequestParam("category") String category,
            @RequestParam("type") String type,
            @RequestParam("monthYear") String monthYear) throws Exception {

        List<CoreType1DTO> coreType1DTOList = core1Service.calculate(category, type, monthYear);

        for (CoreType1DTO coreType1DTO : coreType1DTOList) {
            Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(coreType1DTO.getPeriod());
            String month = monthYearMap.get("monthFormat");
            String year = monthYearMap.get("year");
            String yearMonth = year + month;

            // render thymeleaf
            String htmlContent = renderThymeleafTemplate(coreType1DTO);

            // Generate PDF from HTML content
            byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);

            String fileName = generateFileName(coreType1DTO.getAid(), yearMonth);

            // Save the PDF to a folder on the server
            String outputPath = "/D:/E-Statement/Billing Statement/112023/" + fileName;

            pdfGenerator.savePdfToFile(pdfBytes, outputPath);
        }

        ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload("Success")
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    private String renderThymeleafTemplate(CoreType1DTO coreType1DTO) {
        Context context = new Context();

        return templateEngine.process(coreType1DTO.getBillingTemplate(), context);
    }

    private String generateFileName(String aid, String yearMonth) {
        return aid + "_" + yearMonth + ".pdf";
    }

}
