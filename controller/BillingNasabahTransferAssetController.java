package com.services.billingservice.controller;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.dto.request.CreateNasabahTransferAssetRequest;
import com.services.billingservice.dto.request.UpdateNasabahTransferAssetRequest;
import com.services.billingservice.dto.response.NasabahTransferAssetDTO;
import com.services.billingservice.service.BillingNasabahTransferAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/nasabah-transfer-asset")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class BillingNasabahTransferAssetController {

    private final BillingNasabahTransferAssetService nasabahTransferAssetService;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDTO<String>> create(@RequestBody CreateNasabahTransferAssetRequest request) {

        NasabahTransferAssetDTO nasabahTransferAssetDTO = nasabahTransferAssetService.create(request);

        ResponseDTO<String> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload("Successfully created nasabah transfer asset with id : " + nasabahTransferAssetDTO.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ResponseDTO<NasabahTransferAssetDTO>> getById(@PathVariable("id") String id) {
        NasabahTransferAssetDTO nasabahTransferAssetDTO = nasabahTransferAssetService.getById(id);

        ResponseDTO<NasabahTransferAssetDTO> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(nasabahTransferAssetDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/code")
    public ResponseEntity<ResponseDTO<NasabahTransferAssetDTO>> getBySecurityCode(@RequestParam(name = "securityCode") String securityCode) {
        NasabahTransferAssetDTO nasabahTransferAssetDTO = nasabahTransferAssetService.getBySecurityCode(securityCode);

        ResponseDTO<NasabahTransferAssetDTO> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(nasabahTransferAssetDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseDTO<List<NasabahTransferAssetDTO>>> getAll() {
        List<NasabahTransferAssetDTO> nasabahTransferAssetDTOList = nasabahTransferAssetService.getAll();

        ResponseDTO<List<NasabahTransferAssetDTO>> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(nasabahTransferAssetDTOList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ResponseDTO<NasabahTransferAssetDTO>> updateById(@PathVariable("id") String id,
                                                                           @RequestBody UpdateNasabahTransferAssetRequest request) {
        NasabahTransferAssetDTO nasabahTransferAssetDTO = nasabahTransferAssetService.updateById(id, request);
        ResponseDTO<NasabahTransferAssetDTO> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(nasabahTransferAssetDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/code/{code}")
    public ResponseEntity<ResponseDTO<NasabahTransferAssetDTO>> updateBySecurityCode(@PathVariable("code") String securityCode,
                                                                                     @RequestBody UpdateNasabahTransferAssetRequest request) {
        NasabahTransferAssetDTO nasabahTransferAssetDTO = nasabahTransferAssetService.updateBySecurityCode(securityCode, request);
        ResponseDTO<NasabahTransferAssetDTO> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(nasabahTransferAssetDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteById(@PathVariable("id") String id) {
        String deleteById = nasabahTransferAssetService.deleteById(id);

        ResponseDTO<String> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(deleteById);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/code/{code}")
    public ResponseEntity<ResponseDTO<String>> deleteByCode(@PathVariable("code") String securityCode) {
        String deleteByCode = nasabahTransferAssetService.deleteBySecurityCode(securityCode);

        ResponseDTO<String> response = new ResponseDTO<>();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.toString());
        response.setPayload(deleteByCode);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
