package com.services.billingservice.controller;

import com.services.billingservice.dto.core.CoreType5DTO;
import com.services.billingservice.service.Core5Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Slf4j
@RestController
@RequestMapping(path = "/api/core-5")
@RequiredArgsConstructor
public class Core5Controller {

    private final SpringTemplateEngine templateEngine;
    private final Core5Service core5Service;

//    @PostMapping(path = "/generate-pdf")
//    public ResponseEntity<ResponseDTO<String>> generatePdf(
//            @RequestParam("category") String category,
//            @RequestParam("type") String type,
//            @RequestParam("monthYear") String monthYear
//    ) {
//
//        List<CoreType5DTO> coreType5DTOList = core5Service.calculate(category, type, monthYear);
//
//        for (CoreType5DTO coreType5DTO : coreType5DTOList) {
//
//
//            // render thymeleaf
//            String htmlContent = renderThymeleafTemplate(coreType5DTO);
//
//            // Generate PDF from HTML content
//
//
//            // Save the PDF to a folder on the server
//        }
//
//    }

    private String renderThymeleafTemplate(CoreType5DTO coreType5DTO) {
        Context context = new Context();
        context.setVariable("", coreType5DTO.getSafekeepingValueFrequency());


        String billingTemplate = coreType5DTO.getBillingTemplate();

        return templateEngine.process(billingTemplate, context);
    }


}
