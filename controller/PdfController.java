package com.services.billingservice.controller;

import com.services.billingservice.dto.PdfRequest;
import com.services.billingservice.utils.PdfGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping(path = "/api/pdf")
public class PdfController {

    private final PdfGenerator pdfGenerator;

    private final SpringTemplateEngine templateEngine;

    public PdfController(PdfGenerator pdfGenerator, SpringTemplateEngine templateEngine) {
        this.pdfGenerator = pdfGenerator;
        this.templateEngine = templateEngine;
    }

    @PostMapping("/generate-pdf")
    public String generatePdf(@RequestBody PdfRequest pdfRequest) throws Exception {
        String title = pdfRequest.getTitle();
        String content = pdfRequest.getContent();
        log.info("Title : {}", title);
        log.info("Content : {}", content);

        // Render Thymeleaf template to HTML
        String htmlContent = renderThymeleafTemplate(title, content);

        // Set response headers
//        response.setHeader("Content-Disposition", "inline; filename=generated.pdf");
//        response.setContentType("application/pdf");

        // Generate PDF from HTML content
        byte[] pdfBytes = pdfGenerator.generatePdfFromHtml(htmlContent);

        // Generate a unique file name based on the current date
        String fileName = generateFileName();

        // Save the PDF to a folder on the server
        String outputPath = "/D:/DST/CSA/FILES/IN/" + fileName;

        savePdfToFile(pdfBytes, outputPath);

        // Return a response
        return "PDF saved to: " + outputPath;
    }

    private String renderThymeleafTemplate(String title, String content) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("content", content);

        log.info("Context : {}", context);
        return templateEngine.process("myTemplate", context);
    }

    private void savePdfToFile(byte[] pdfBytes, String outputPath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(new File(outputPath))) {
            fos.write(pdfBytes);
            fos.flush();
        }
    }

    private String generateFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        String formattedDate = dateFormat.format(new Date());
        return "PDF_" + formattedDate + ".pdf";
    }

}
