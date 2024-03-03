package com.services.billingservice.controller.compliance;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.service.compliance.ReksadanaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/compliance/reksadana")
public class ReksadanaController {
    @Autowired
    ReksadanaService reksadanaService;

//    @PostMapping
//    public ResponseEntity<ResponseDTO> save(@RequestBody MappingKINVRequestDTO mappingKINVRequestDTO)  {
//        return reksadanaService.insertReksadanaManual(mappingKINVRequestDTO);
//    }

//    @PutMapping("/upload/{param}")
//    public ResponseEntity<ResponseDTO> uploadFileReksadana(@PathVariable String param, @RequestBody List<Map<String, String>> reksadanaList) {
//        return reksadanaService.insertReksadanaUpload(param, reksadanaList);
//    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO> getAllReksadana()  {
        return reksadanaService.findAllReksadana();
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> searchPortfolio(@RequestParam String findByCode)  {
        return reksadanaService.searchReksadana(findByCode);
    }

//    @GetMapping("/{code}")
//    public ResponseEntity<ResponseDTO> getByCode(@PathVariable String code)  {
//        return reksadanaService.getByCode(code);
//    }

//    @GetMapping("view")
//    public ResponseEntity<ResponseDTO> getPendingReksadanaByCode(@RequestParam String code)  {
//        return reksadanaService.getPendingReksadanaByCode(code);
//    }

//    @PutMapping("/delete/{code}")
//    public ResponseEntity<ResponseDTO> deleteById(@PathVariable String code)  {
//        return reksadanaService.deleteByCode(code);
//    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseDTO> getAllPendingData() {
        return reksadanaService.allPendingDataReksadana();
    }

    @PostMapping("/approve")
    public ResponseEntity<ResponseDTO> approveDataWithCode(@RequestBody Map<String, List<String>> codeList) {
        return reksadanaService.approveDataReksadana(codeList);
    }
//
//    @PostMapping("/reject")
//    public ResponseEntity<ResponseDTO> rejectDataWithCode(@RequestBody Map<String, List<String>> codeList) {
//        return reksadanaService.rejectDataReksadana(codeList);
//    }
}
