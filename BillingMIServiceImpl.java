package com.services.billingservice.service.impl;

import com.services.billingservice.dto.BillingMIDTO;
import com.services.billingservice.dto.mi.CreateInvestmentManagementRequest;
import com.services.billingservice.dto.mi.UploadInvestmentManagementRequest;
import com.services.billingservice.exception.BadRequestException;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingMI;
import com.services.billingservice.repository.BillingMIRepository;
import com.services.billingservice.service.BillingMIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingMIServiceImpl implements BillingMIService {

    private final BillingMIRepository billingMIRepository;

    @Override
    public BillingMIDTO create(CreateInvestmentManagementRequest request) {
        String code = request.getCode();
        String name = request.getName();
        String email = request.getEmail();
        String alamat1 = request.getAlamat1();
        String alamat2 = request.getAlamat2();
        String alamat3 = request.getAlamat3();
        String alamat4 = request.getAlamat4();

        BillingMI billingMI = BillingMI.builder()
                .code(code)
                .name(name)
                .email(email)
                .alamat1(alamat1)
                .alamat2(alamat2)
                .alamat3(alamat3)
                .alamat4(alamat4)
                .build();

        BillingMI dataSaved = billingMIRepository.save(billingMI);

        return  mapToDTO(dataSaved);
    }

    @Override
    public BillingMIDTO getByCode(String code) {
        System.out.println("getByCode");
        BillingMI billingMI = billingMIRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Data Not Found"));

        return mapToDTO(billingMI);
    }

    @Override
    public List<BillingMIDTO> getAll() {
        List<BillingMI> billingMI = billingMIRepository.findAll();

        return mapToBillingMIDTOList(billingMI);
    }

    @Override
    public BillingMIDTO updateByCode(String code, CreateInvestmentManagementRequest request) {
        BillingMI billingMI = billingMIRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("Data not found"));

        billingMI.setCode(request.getCode());
        billingMI.setName(request.getName());
        billingMI.setEmail(request.getEmail());
        billingMI.setAlamat1(request.getAlamat1());
        billingMI.setAlamat2(request.getAlamat2());
        billingMI.setAlamat3(request.getAlamat3());
        billingMI.setAlamat4(request.getAlamat4());


        BillingMI datasaved = billingMIRepository.save(billingMI);
        return mapToDTO(datasaved);
    }

    @Override
    public List<BillingMIDTO> upload(List<UploadInvestmentManagementRequest> request) {
        List<BillingMI> billingMI = mapToBillingMIUploadList(request);
        List<BillingMI> billingMI1 = billingMIRepository.saveAll(billingMI);
        return mapToBillingMIDTOList(billingMI1);
    }

    @Override
    public List<BillingMIDTO> updateUploadByCode(List<UploadInvestmentManagementRequest> request) {
        List<BillingMI> billingMIList = new ArrayList<>();

        if (0 == request.size()) {
            new Error("0 size");
        } else {
            for (UploadInvestmentManagementRequest billingMIRequest : request) {
                // lakukan update
                BillingMI billingMI = billingMIRepository.findByCode(billingMIRequest.getCode())
                        .orElseThrow(() -> new DataNotFoundException("Data not found"));

                if (billingMIRequest.getCode() != null) {
                    billingMI.setCode(billingMIRequest.getCode());

                }
                if (billingMIRequest.getName() != null) {
                    billingMI.setName(billingMIRequest.getName());
                }
                if (billingMIRequest.getName() != null) {
                    billingMI.setName(billingMIRequest.getName());
                }
                if (billingMIRequest.getEmail() != null) {
                    billingMI.setEmail(billingMIRequest.getEmail());
                }
                if (billingMIRequest.getAlamat1() != null) {
                    billingMI.setAlamat1(billingMIRequest.getAlamat1());
                }
                if (billingMIRequest.getAlamat2() != null) {
                    billingMI.setAlamat2(billingMIRequest.getAlamat2());
                }
                if (billingMIRequest.getAlamat3() != null) {
                    billingMI.setAlamat3(billingMIRequest.getAlamat3());
                }
                if (billingMIRequest.getAlamat4() != null) {
                    billingMI.setAlamat4(billingMIRequest.getAlamat4());
                }
                // save to the database
                BillingMI save = billingMIRepository.save(billingMI);

                billingMIList.add(save);
            }
        }
        return mapToBillingMIDTOList(billingMIList);
    }

    @Override
    public String deleteById(String id) {
        BillingMI billingMI = billingMIRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new DataNotFoundException("MI data with id '" + id + "' not found"));
        billingMIRepository.delete(billingMI);
        return "Successfully delete MI with id: " + billingMI.getId();
    }

    @Override
    public String deleteByCode(String code) {
        BillingMI billingMI = billingMIRepository.findByCode(code)
                .orElseThrow(() -> new DataNotFoundException("MI data with code '" + code + "' not found"));
        billingMIRepository.delete(billingMI);
        return "Successfully delete MI with code: " + billingMI.getCode();
    }

    @Override
    public Boolean checkExistByCode(String miCode) {
        Boolean existsByCode = billingMIRepository.existsByCode(miCode);
        // TRUE itu artinya MI Code already di table
        log.info("Exist by code: {}", existsByCode);
        return existsByCode;
    }

    @Override
    public String createList(List<CreateInvestmentManagementRequest> requestList) {
        int totalSuccessSaved = 0;

        for (CreateInvestmentManagementRequest request : requestList) {
            // check mi code apakah already taken
            Boolean checkExistByCode = checkExistByCode(request.getCode());
            if (checkExistByCode) {
                // throw new BadRequestException("MI code '" + request.getCode() + "' is already taken");
                log.info("MI Code '" + request.getCode() + "' is already taken");
            } else {
                BillingMI billingMI = BillingMI.builder()
                        .code(request.getCode())
                        .name(request.getName())
                        .email(request.getEmail())
                        .alamat1(request.getAlamat1())
                        .alamat2(request.getAlamat2())
                        .alamat3(request.getAlamat3())
                        .alamat4(request.getAlamat4())
                        .build();

                BillingMI save = billingMIRepository.save(billingMI);
                log.info("MI Saved: {}", save.getId());
                totalSuccessSaved++;
            }
        }
        return "Successfully create MI with total: " + totalSuccessSaved;
    }

    private BillingMIDTO mapToDTO(BillingMI billingMI) {
        return BillingMIDTO.builder()
                .id(String.valueOf(billingMI.getId()))
                .code(billingMI.getCode())
                .name(billingMI.getName())
                .email(billingMI.getEmail())
                .alamat1(billingMI.getAlamat1())
                .alamat2(billingMI.getAlamat2())
                .alamat3(billingMI.getAlamat3())
                .alamat4(billingMI.getAlamat4())
                .build();
    }

    private List<BillingMIDTO> mapToBillingMIDTOList(List<BillingMI> billingMIList) {
        return billingMIList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private List<BillingMI> mapToBillingMIUploadList(List<UploadInvestmentManagementRequest> billingMIList) {
        return billingMIList.stream()
                .map(this::mapToBillingMIUpload)
                .collect(Collectors.toList());

    }

    private BillingMI mapToBillingMIUpload(UploadInvestmentManagementRequest request) {
        return BillingMI.builder()
                .code(request.getCode())
                .name(request.getName())
                .email(request.getEmail())
                .alamat1(request.getAlamat1())
                .alamat2(request.getAlamat2())
                .alamat3(request.getAlamat3())
                .alamat4(request.getAlamat4())
                .build();
    }
}
