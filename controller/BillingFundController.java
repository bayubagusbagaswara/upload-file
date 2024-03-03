package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.fund.BillingFundDTO;
import com.services.billingservice.dto.fund.FeeReportRequest;
import com.services.billingservice.service.BillingFundService;
import com.services.billingservice.utils.ConvertDateUtil;
import com.services.billingservice.utils.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/billing/fund")
@RequiredArgsConstructor
public class BillingFundController {

    private final BillingFundService billingFundService;
    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;

    @PostMapping(path = "/generate")
    public ResponseEntity<ResponseDTO<List<BillingFundDTO>>> generate(
            @RequestBody List<FeeReportRequest> reportRequests,
            @RequestParam("date") String date) {

        List<BillingFundDTO> billingFundDTOList = billingFundService.generateBillingFund(reportRequests,date);
        ResponseDTO<List<BillingFundDTO>> responseDTO = ResponseDTO.<List<BillingFundDTO>>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(billingFundDTOList)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> generatePdf(
            @RequestBody List<FeeReportRequest> reportRequests,
            @RequestParam("date") String date) throws Exception {

        // date harus Nov 2023
        List<BillingFundDTO> billingFundDTOList = billingFundService.generateBillingFund(reportRequests,date);

        // Generate a unique file name based on the current date
        for (BillingFundDTO billingFundDTO : billingFundDTOList) {
            Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(billingFundDTO.getPeriod());
            String month = monthYearMap.get("monthFormat");
            String year = monthYearMap.get("year");
            String yearMonth = year + month;

            // render thymeleaf
            String htmlContent = renderThymeleafTemplate(billingFundDTO);

            // Generate PDF from HTML content
            byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);

            String fileName = generateFileName(billingFundDTO.getPortfolioCode(), yearMonth);

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

    private String renderThymeleafTemplate(BillingFundDTO billingFundDTO) {
        Context context = new Context();
        context.setVariable("amountDueAccrualCustody", billingFundDTO.getAmountDueAccrualCustody());
        context.setVariable("valueFrequencyS4", billingFundDTO.getValueFrequencyS4());
        context.setVariable("s4Fee", billingFundDTO.getS4Fee());
        context.setVariable("amountDueS4", billingFundDTO.getAmountDueS4());
        context.setVariable("totalNominalBeforeTax", billingFundDTO.getTotalNominalBeforeTax());
        context.setVariable("taxFee", billingFundDTO.getTaxFee());
        context.setVariable("amountDueTax", billingFundDTO.getAmountDueTax());
        context.setVariable("valueFrequencyKSEI", billingFundDTO.getValueFrequencyKSEI());
        context.setVariable("kseiFee", billingFundDTO.getKseiFee());
        context.setVariable("amountDueKsei", billingFundDTO.getAmountDueKSEI());
        context.setVariable("totalAmountDue", billingFundDTO.getTotalAmountDue());

        // template engine tergantung pada Type Template nya

        // return templateEngine.process("thymeleaf", context);
        return templateEngine.process("fundSampleTemplate", context);
    }

    private String generateFileName(String portfolioCode, String yearMonth) {
        return "MANAGEMENT_INVESTMENT" + "_" + portfolioCode + "_" + yearMonth + ".pdf";
    }
}
