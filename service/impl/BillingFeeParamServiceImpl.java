package com.services.billingservice.service.impl;

import com.services.billingservice.dto.BillingFeeParamDTO;
import com.services.billingservice.dto.request.BillingFeeParamRequest;
import com.services.billingservice.dto.request.CreateBillingCustomerRequest;
import com.services.billingservice.dto.response.BillingCustomerDTO;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingCustomer;
import com.services.billingservice.model.BillingFeeParam;
import com.services.billingservice.repository.BillingCustomerRepository;
import com.services.billingservice.repository.BillingFeeParamRepository;
import com.services.billingservice.service.BillingCustomerService;
import com.services.billingservice.service.BillingFeeParamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingFeeParamServiceImpl implements BillingFeeParamService {

    private final BillingFeeParamRepository billingFeeParamRepository;

    @Override
    public BillingFeeParamDTO create(BillingFeeParamRequest request) {
        String feeCode = request.getFeeCode();
        String feeName = request.getFeeName();
        double value = request.getValue();
        String description = request.getDescription();

        BillingFeeParam billingFeeParam = BillingFeeParam.builder()
                .feeCode(feeCode)
                .feeName(feeName)
                .value(value)
                .description(description)
                .build();

        BillingFeeParam dataSaved = billingFeeParamRepository.save(billingFeeParam);
        return mapToDTO(dataSaved);

    }

    private BillingFeeParamDTO mapToDTO(BillingFeeParam billingFeeParam) {
        return BillingFeeParamDTO.builder().build().builder()
                .id(String.valueOf(billingFeeParam.getId()))
                .feeCode(billingFeeParam.getFeeCode())
                .feeName(billingFeeParam.getFeeName())
                .value(billingFeeParam.getValue())
                .description(billingFeeParam.getDescription())
                .build();
    }

    private List<BillingFeeParamDTO> mapToFeeParamDTOList(List<BillingFeeParam> feeParamList) {
        return feeParamList.stream()
                .map(this::maptoFeeParamDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillingFeeParamDTO> upload(List<BillingFeeParamRequest> request) {
        List<BillingFeeParam> billingFeeParams = mapToFeeParamList(request);
        List<BillingFeeParam> billingFeeParams1 = billingFeeParamRepository.saveAll(billingFeeParams);
        return mapToFeeParamDTOList(billingFeeParams1);
    }


    private List<BillingFeeParam> mapToFeeParamList(List<BillingFeeParamRequest> feeParamList) {
        return feeParamList.stream()
                .map(this::mapToFeeParam)
                .collect(Collectors.toList());

    }

    private BillingFeeParam mapToFeeParam(BillingFeeParamRequest request) {
        return BillingFeeParam.builder()
                .feeCode(request.getFeeCode())
                .feeName(request.getFeeName())
                .value(request.getValue())
                .description(request.getDescription())
                .build();
    }

    private BillingFeeParamDTO maptoFeeParamDTO(BillingFeeParam feeParam) {
        return BillingFeeParamDTO.builder()
                .id(String.valueOf(feeParam.getId()))
                .feeCode(feeParam.getFeeCode())
                .feeName(feeParam.getFeeName())
                .value(feeParam.getValue())
                .description((feeParam.getDescription()))
                .build();
    }


    @Override
    public BillingFeeParamDTO getByCode(String code) {
        return null;
    }

    @Override
    public List<BillingFeeParamDTO> getAll() {
        return null;
    }

    @Override
    public BillingFeeParamDTO updateByCode(String code, BillingFeeParamRequest request) {


        BillingFeeParam billingFeeParam = billingFeeParamRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        billingFeeParam.setFeeCode(request.getFeeCode());
        billingFeeParam.setFeeName(request.getFeeName());
        billingFeeParam.setValue(request.getValue());
        billingFeeParam.setDescription(request.getDescription());

        BillingFeeParam datasaved = billingFeeParamRepository.save(billingFeeParam);
        return mapToDTO(datasaved);
    }

    @Override
    public List<BillingFeeParamDTO> updateUploadByCode(List<BillingFeeParamRequest> request) {
        // code 1, 2, 3, 4, 5
        // checking request is null
        List<BillingFeeParam> feeParamList = new ArrayList<>();

        if (0 == request.size()) {
            // request is null
        } else {
            for (BillingFeeParamRequest billingFeeParamRequest : request) {
                    // lakukan update
                BillingFeeParam billingFeeParam = billingFeeParamRepository.findByCode(billingFeeParamRequest.getFeeCode())
                        .orElseThrow(() -> new DataNotFoundException("Data not found"));

                billingFeeParam.setValue(billingFeeParamRequest.getValue());
//                billingFeeParam.setFeeCode(billingFeeParamRequest.getFeeCode());
//                billingFeeParam.setDescription(billingFeeParamRequest.getDescription());
                // save to the database
                BillingFeeParam save = billingFeeParamRepository.save(billingFeeParam);

                feeParamList.add(save);
            }
        }
        return mapToFeeParamDTOList(feeParamList);
    }
}
