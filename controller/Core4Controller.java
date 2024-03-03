package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.core.CoreType4DTO;
import com.services.billingservice.service.Core4Service;
import com.services.billingservice.utils.ConvertDateUtil;
import com.services.billingservice.utils.PdfGenerator;
import com.services.billingservice.utils.StringUtil;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/core-4")
@RequiredArgsConstructor
public class Core4Controller {

    private final SpringTemplateEngine templateEngine;
    private final PdfGenerator pdfGenerator;
    private final Core4Service core4Service;

    @GetMapping(path = "/generate-pdf")
    public ResponseEntity<ResponseDTO<String>> calculate(
            @RequestParam("category") String category,
            @RequestParam("type") String type,
            @RequestParam("monthYear") String monthYear) throws Exception {

        log.info("[Core 4 Controller] Category : {}, Type : {}, Month Year : {}",
                category, type, monthYear);

        String categoryUpperCase = category.toUpperCase();
        String typeUpperCase = StringUtil.replaceBlanksWithUnderscores(type).toUpperCase(); // TYPE_1


        List<CoreType4DTO> coreType4DTOList = core4Service.calculate(
                categoryUpperCase, typeUpperCase, monthYear
        );

        for (CoreType4DTO coreType4DTO : coreType4DTOList) {
            Map<String, String> monthYearMap = ConvertDateUtil.getMonthYear(coreType4DTO.getPeriod());
            String month = monthYearMap.get("monthFormat");
            String year = monthYearMap.get("year");

            // render thymeleaf
            String htmlContent = renderThymeleafTemplate(coreType4DTO);

            // Generate PDF from HTML content
            byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);

            String fileName;

            if (coreType4DTO.getBillingTemplate().equalsIgnoreCase("TEMPLATE_5")) {
                fileName = generateFileNameEB(coreType4DTO.getAid(), month, year);
            } else {
                fileName = generateFileNameItama(coreType4DTO.getAid(), month, year);
            }

            // Save the PDF to a folder on the server
            String outputPath = "/D:/E-Statement/Billing Statement/112023/" + fileName;

            savePdfToFile(pdfBytes, outputPath);
        }


        ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload("Success")
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }


    private String renderThymeleafTemplate(CoreType4DTO coreType4DTO) {
        Context context = new Context();
        context.setVariable("aid", coreType4DTO.getAid());
        context.setVariable("billingTemplate", coreType4DTO.getBillingTemplate());
        context.setVariable("period", coreType4DTO.getPeriod());

        // EB
        context.setVariable("kseiSafekeepingAmountDue", coreType4DTO.getKseiSafekeepingAmountDue());
        context.setVariable("kseiTransactionValueFrequency", coreType4DTO.getKseiTransactionValueFrequency());
        context.setVariable("kseiTransactionFee", coreType4DTO.getKseiTransactionFee());
        context.setVariable("kseiTransactionAmountDue", coreType4DTO.getKseiTransactionAmountDue());
        context.setVariable("totalAmountDueEB", coreType4DTO.getTotalAmountDueEB());

        // ITAMA
        context.setVariable("safekeepingFrequency", coreType4DTO.getSafekeepingFrequency());
        context.setVariable("safekeepingFee", coreType4DTO.getSafekeepingFee());
        context.setVariable("safekeepingAmountDue", coreType4DTO.getSafekeepingAmountDue());
        context.setVariable("vatFee", coreType4DTO.getVatFee());
        context.setVariable("vatAmountDue", coreType4DTO.getVatAmountDue());
        context.setVariable("totalAmountDueItama", coreType4DTO.getTotalAmountDueItama());

        log.info("[Render Thymeleaf Template] Billing Template : {}", coreType4DTO.getBillingTemplate());
        return templateEngine.process(coreType4DTO.getBillingTemplate().toLowerCase(), context);
    }

    private String generateFileNameEB(String aid, String month, String year) {
        // 17OBAL_EB_yearmonth.pdf
        return aid + "_" + "EB" + "_" + year + month + ".pdf";
    }

    private String generateFileNameItama(String aid, String month, String year) {
        // 17OBAL_EB_yearmonth.pdf
        return aid + "_" + "Itama" + "_" + year + month + ".pdf";
    }

    private void savePdfToFile(byte[] pdfBytes, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            fos.write(pdfBytes);
            fos.flush();
        }
    }

}
