package com.services.billingservice.service.compliance;

import com.services.billingservice.dto.ResponseDTO;
import com.services.billingservice.enums.ApprovalStatus;
import com.services.billingservice.model.compliance.*;
import com.services.billingservice.repository.compliance.*;
import com.services.billingservice.utils.UserIdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ReksadanaService {

    @Autowired
    ReksadanaRepository reksadanaRepository;
//    @Autowired
//    ReksadanaTypeRepository reksadanaTypeRepository;
//    @Autowired
//    KINVReksadanaRepository kinvReksadanaRepository;
//    @Autowired
//    MasterKebijakanInvestasiRepository masterKebijakanInvestasiRepository;
//    @Autowired
//    PortfolioKINVGroupingRepository portfolioKINVGroupingRepository;
//    @Autowired
//    ComplianceDataChangeRepository complianceDataChangeRepository;


//    @Transactional
//    public ResponseEntity<ResponseDTO> insertReksadanaManual(MappingKINVRequestDTO mappingKINVRequestDTO) {
//        ResponseDTO responseDto = new ResponseDTO();
//        try {
//            String message = "Success insert new reksadana";
//            ReksadanaType reksadanaType = reksadanaTypeRepository.findByReksadanaType(mappingKINVRequestDTO.getReksadanaType());
//            Reksadana reksadana = reksadanaRepository.findByCode(mappingKINVRequestDTO.getCode());
//
//            if (reksadana == null) {
//                reksadana = new Reksadana();
//                reksadana.setApprovalStatus(ApprovalStatus.Pending);
//                reksadana.setInputDate(new Date());
//                reksadana.setInputerId(UserIdUtil.getUser());
//                reksadana.setDelete(false);
//                reksadana.setCode(mappingKINVRequestDTO.getCode());
//                reksadana.setName(mappingKINVRequestDTO.getName());
//                reksadana.setAddress(mappingKINVRequestDTO.getAddress());
//                reksadana.setExternalCode(mappingKINVRequestDTO.getExternalCode());
//                reksadana.setEmail(mappingKINVRequestDTO.getEmail());
//                reksadana.setPercentageModal(mappingKINVRequestDTO.getPercentageModal());
//                reksadana.setReksadanaType(reksadanaType);
//                reksadana.setTnabMinimum(mappingKINVRequestDTO.getTnabMinimum());
//                reksadana.setManajerInvestasi(mappingKINVRequestDTO.getManajerInvestasi());
//                reksadana.setPic(mappingKINVRequestDTO.getPic());
//                reksadana.setSyariah(mappingKINVRequestDTO.isSyariah());
//                reksadana.setConventional(mappingKINVRequestDTO.isConventional());
//                reksadanaRepository.save(reksadana);
//
//                storeKebijakanRd(reksadana, mappingKINVRequestDTO);
//
//            } else {
//                if (reksadana.isDelete()){
//                    throw new Exception("This reksadana have registered before! Use another code!");
//                } else {
//                    Reksadana reksadanaAfter = new Reksadana();
//                    reksadanaAfter.setInputDate(new Date());
//                    reksadanaAfter.setInputerId(UserIdUtil.getUser());
//                    reksadanaAfter.setDelete(false);
//                    reksadanaAfter.setName(mappingKINVRequestDTO.getName());
//                    reksadanaAfter.setAddress(mappingKINVRequestDTO.getAddress());
//                    reksadanaAfter.setExternalCode(mappingKINVRequestDTO.getExternalCode());
//                    reksadanaAfter.setEmail(mappingKINVRequestDTO.getEmail());
//                    reksadanaAfter.setPercentageModal(mappingKINVRequestDTO.getPercentageModal());
//                    reksadanaAfter.setTnabMinimum(mappingKINVRequestDTO.getTnabMinimum());
//                    reksadanaAfter.setReksadanaType(reksadanaType);
//                    reksadanaAfter.setManajerInvestasi(mappingKINVRequestDTO.getManajerInvestasi());
//                    reksadanaAfter.setPic(mappingKINVRequestDTO.getPic());
//                    reksadanaAfter.setSyariah(mappingKINVRequestDTO.isSyariah());
//                    reksadanaAfter.setConventional(mappingKINVRequestDTO.isConventional());
//
//                    ObjectMapper Obj = new ObjectMapper();
//                    String jsonbefore = Obj.writeValueAsString(reksadana);
//                    ObjectMapper ObjAfter = new ObjectMapper();
//                    String jsonAfter = ObjAfter.writeValueAsString(reksadanaAfter);
//
//                    ComplianceDataChange dataChange = new ComplianceDataChange();
//                    dataChange.setApprovalStatus(ApprovalStatus.Pending);
//                    dataChange.setInputerId(UserIdUtil.getUser());
//                    dataChange.setInputDate(new Date());
//                    dataChange.setAction(ChangeAction.Edit);
//                    dataChange.setEntityId(reksadana.getCode());
//                    dataChange.setTableName("comp_reksadana");
//                    dataChange.setEntityClassName(Reksadana.class.getName());
//                    dataChange.setDataBefore(jsonbefore);
//                    dataChange.setDataChange(jsonAfter);
//                    complianceDataChangeRepository.save(dataChange);
//
//                    storeKebijakanRd(reksadana, mappingKINVRequestDTO);
//
//                    if (mappingKINVRequestDTO.getIdToBeDelete() != "") {
//                        deleteKinvReksadana(mappingKINVRequestDTO.getIdToBeDelete(), UserIdUtil.getUser());
//                    }
//                }
//
//                message = "Success update reksadana";
//            }
//
//            responseDto.setCode(HttpStatus.OK.toString());
//            responseDto.setMessage("OK");
//            responseDto.setPayload(message);
//
//        } catch (Exception e) {
//            responseDto.setCode(HttpStatus.BAD_REQUEST.toString());
//            responseDto.setMessage("FAILED");
//            responseDto.setPayload(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
//    }
//
//    private void deleteKinvReksadana(String idToBeDelete, String approverId) {
//        String[] array = idToBeDelete.split(",");
//        List<String> listId = Arrays.asList(array);
//        try {
//            for (String id : listId) {
//                KINVReksadana kinvReksadana = kinvReksadanaRepository.findById(Long.valueOf(id)).orElseThrow(() -> new RuntimeException("Data Not Found!"));
//
//                ObjectMapper Obj = new ObjectMapper();
//                String jsonbefore = Obj.writeValueAsString(kinvReksadana);
//                ObjectMapper ObjAfter = new ObjectMapper();
//                String jsonAfter = ObjAfter.writeValueAsString(kinvReksadana);
//
//                ComplianceDataChange dataChange = new ComplianceDataChange();
//                dataChange.setApprovalStatus(ApprovalStatus.Pending);
//                dataChange.setInputerId(UserIdUtil.getUser());
//                dataChange.setInputDate(new Date());
//                dataChange.setAction(ChangeAction.Delete);
//                dataChange.setEntityId(String.valueOf(kinvReksadana.getId()));
//                dataChange.setTableName("comp_kinv_reksadana");
//                dataChange.setEntityClassName(KINVReksadana.class.getName());
//                dataChange.setDataBefore(jsonbefore);
//                dataChange.setDataChange(jsonAfter);
//                complianceDataChangeRepository.save(dataChange);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    private void storeKebijakanRd(Reksadana reksadana, MappingKINVRequestDTO mappingKINVRequestDTO) {
//        List<KINVReksadanaRequestDTO> kinvReksadanaRequestDTOList = mappingKINVRequestDTO.getKinvReksadanaList();
//        try{
//            for (KINVReksadanaRequestDTO kinvReksadanaRequestDTO : kinvReksadanaRequestDTOList) {
//                KINVReksadana kinvReksadana = new KINVReksadana();
//                if (kinvReksadanaRequestDTO.getId() == null) {
//                    System.out.println("New KINV Reksadana");
//                    kinvReksadana.setKinvCode(masterKebijakanInvestasiRepository.findByKinvCode(kinvReksadanaRequestDTO.getKinvCode()));
//                    kinvReksadana.setApprovalStatus(ApprovalStatus.Pending);
//                    kinvReksadana.setInputDate(new Date());
//                    kinvReksadana.setInputerId(UserIdUtil.getUser());
//                    kinvReksadana.setReksadanaCode(reksadana);
//                    kinvReksadana.setRdExternalCode(mappingKINVRequestDTO.getExternalCode());
//                    kinvReksadana.setKinvMin(kinvReksadanaRequestDTO.getKinvMin());
//                    kinvReksadana.setKinvMax(kinvReksadanaRequestDTO.getKinvMax());
//                    kinvReksadanaRepository.save(kinvReksadana);
//                } else {
//                    System.out.println("Update KINV Reksadana");
//                    kinvReksadana = kinvReksadanaRepository.findById(kinvReksadanaRequestDTO.getId()).orElseThrow(() -> new RuntimeException("Data Not Found!"));
//                    KINVReksadana kinvReksadanaAfter= new KINVReksadana();
//                    kinvReksadanaAfter.setKinvCode(masterKebijakanInvestasiRepository.findByKinvCode(kinvReksadanaRequestDTO.getKinvCode()));
//                    kinvReksadanaAfter.setInputDate(new Date());
//                    kinvReksadanaAfter.setInputerId(UserIdUtil.getUser());
//                    kinvReksadanaAfter.setReksadanaCode(reksadana);
//                    kinvReksadanaAfter.setRdExternalCode(mappingKINVRequestDTO.getExternalCode());
//                    kinvReksadanaAfter.setKinvMin(kinvReksadanaRequestDTO.getKinvMin());
//                    kinvReksadanaAfter.setKinvMax(kinvReksadanaRequestDTO.getKinvMax());
//
//                    ObjectMapper Obj = new ObjectMapper();
//                    String jsonbefore = Obj.writeValueAsString(kinvReksadana);
//                    ObjectMapper ObjAfter = new ObjectMapper();
//                    String jsonAfter = ObjAfter.writeValueAsString(kinvReksadanaAfter);
//
//                    ComplianceDataChange dataChange = new ComplianceDataChange();
//                    dataChange.setApprovalStatus(ApprovalStatus.Pending);
//                    dataChange.setInputerId(UserIdUtil.getUser());
//                    dataChange.setInputDate(new Date());
//                    dataChange.setAction(ChangeAction.Edit);
//                    dataChange.setEntityId(String.valueOf(kinvReksadana.getId()));
//                    dataChange.setTableName("comp_kinv_reksadana");
//                    dataChange.setEntityClassName(KINVReksadana.class.getName());
//                    dataChange.setDataBefore(jsonbefore);
//                    dataChange.setDataChange(jsonAfter);
//                    complianceDataChangeRepository.save(dataChange);
//                }
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    public ResponseEntity<ResponseDTO> getByCode(String code) {
//        Reksadana reksadana = reksadanaRepository.findByCode(code);
//        List<KINVReksadana> kinvReksadanas = kinvReksadanaRepository.findAllByApprovalStatusAndReksadanaCode(ApprovalStatus.Approved, reksadana);
//        List<KINVReksadanaRequestDTO> kinvReksadanaRequestDTOList = new ArrayList<KINVReksadanaRequestDTO>();
//        for (KINVReksadana kinvReksadana : kinvReksadanas) {
//            KINVReksadanaRequestDTO kinvReksadanaRequestDTO = new KINVReksadanaRequestDTO();
//            kinvReksadanaRequestDTO.setId(kinvReksadana.getId());
//            kinvReksadanaRequestDTO.setKinvCode(kinvReksadana.getKinvCode().getKinvCode());
//            kinvReksadanaRequestDTO.setKinvMin(kinvReksadana.getKinvMin());
//            kinvReksadanaRequestDTO.setKinvMax(kinvReksadana.getKinvMax());
//            kinvReksadanaRequestDTO.setReksadanaCode(kinvReksadana.getReksadanaCode().getCode());
//            kinvReksadanaRequestDTOList.add(kinvReksadanaRequestDTO);
//        }
//
//        MappingKINVRequestDTO mappingKINVRequestDTO = new MappingKINVRequestDTO();
//        mappingKINVRequestDTO.setCode(reksadana.getCode());
//        mappingKINVRequestDTO.setAddress(reksadana.getAddress());
//        mappingKINVRequestDTO.setEmail(reksadana.getEmail());
//        mappingKINVRequestDTO.setConventional(reksadana.isConventional());
//        mappingKINVRequestDTO.setExternalCode(reksadana.getExternalCode());
//        mappingKINVRequestDTO.setManajerInvestasi(reksadana.getManajerInvestasi());
//        mappingKINVRequestDTO.setName(reksadana.getName());
//        mappingKINVRequestDTO.setPercentageModal(reksadana.getPercentageModal());
//        mappingKINVRequestDTO.setPic(reksadana.getPic());
//        mappingKINVRequestDTO.setSyariah(reksadana.isSyariah());
//        mappingKINVRequestDTO.setTnabMinimum(reksadana.getTnabMinimum());
//        mappingKINVRequestDTO.setReksadanaType(reksadana.getReksadanaType().getReksadanaType());
//        mappingKINVRequestDTO.setKinvReksadanaList(kinvReksadanaRequestDTOList);
//
//        ResponseDTO responseDto = new ResponseDTO();
//        responseDto.setCode(HttpStatus.OK.toString());
//        responseDto.setMessage("OK");
//        responseDto.setPayload(mappingKINVRequestDTO);
//
//        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
//    }
//
//
//    public ResponseEntity<ResponseDTO> getPendingReksadanaByCode(String code) {
//        Reksadana reksadana = reksadanaRepository.findByCode(code);
//        List<KINVReksadana> kinvReksadanas = kinvReksadanaRepository.findAllByApprovalStatusAndReksadanaCode(ApprovalStatus.Pending, reksadana);
//        List<KINVReksadanaRequestDTO> kinvReksadanaRequestDTOList = new ArrayList<KINVReksadanaRequestDTO>();
//        for (KINVReksadana kinvReksadana : kinvReksadanas) {
//            KINVReksadanaRequestDTO kinvReksadanaRequestDTO = new KINVReksadanaRequestDTO();
//            kinvReksadanaRequestDTO.setId(kinvReksadana.getId());
//            kinvReksadanaRequestDTO.setKinvCode(kinvReksadana.getKinvCode().getKinvCode());
//            kinvReksadanaRequestDTO.setKinvMin(kinvReksadana.getKinvMin());
//            kinvReksadanaRequestDTO.setKinvMax(kinvReksadana.getKinvMax());
//            kinvReksadanaRequestDTO.setReksadanaCode(kinvReksadana.getReksadanaCode().getCode());
//            kinvReksadanaRequestDTOList.add(kinvReksadanaRequestDTO);
//        }
//
//        MappingKINVRequestDTO mappingKINVRequestDTO = new MappingKINVRequestDTO();
//        mappingKINVRequestDTO.setCode(reksadana.getCode());
//        mappingKINVRequestDTO.setAddress(reksadana.getAddress());
//        mappingKINVRequestDTO.setEmail(reksadana.getEmail());
//        mappingKINVRequestDTO.setConventional(reksadana.isConventional());
//        mappingKINVRequestDTO.setExternalCode(reksadana.getExternalCode());
//        mappingKINVRequestDTO.setManajerInvestasi(reksadana.getManajerInvestasi());
//        mappingKINVRequestDTO.setName(reksadana.getName());
//        mappingKINVRequestDTO.setPercentageModal(reksadana.getPercentageModal());
//        mappingKINVRequestDTO.setPic(reksadana.getPic());
//        mappingKINVRequestDTO.setSyariah(reksadana.isSyariah());
//        mappingKINVRequestDTO.setTnabMinimum(reksadana.getTnabMinimum());
//        mappingKINVRequestDTO.setReksadanaType(reksadana.getReksadanaType().getReksadanaType());
//        mappingKINVRequestDTO.setKinvReksadanaList(kinvReksadanaRequestDTOList);
//
//        ResponseDTO responseDto = new ResponseDTO();
//        responseDto.setCode(HttpStatus.OK.toString());
//        responseDto.setMessage("OK");
//        responseDto.setPayload(mappingKINVRequestDTO);
//
//        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
//    }

//    public ResponseEntity<ResponseDTO> deleteByCode(String code) {
//        ResponseDTO responseDto = new ResponseDTO();
//        try {
//            Reksadana reksadana = reksadanaRepository.findByCode(code);
//            Reksadana ReksadanaAfter = new Reksadana();
//            ReksadanaAfter.setDelete(true);
//
//            ObjectMapper Obj = new ObjectMapper();
//            String jsonbefore = Obj.writeValueAsString(reksadana);
//            ObjectMapper ObjAfter = new ObjectMapper();
//            String jsonAfter = ObjAfter.writeValueAsString(ReksadanaAfter);
//
//            ComplianceDataChange dataChange = new ComplianceDataChange();
//            dataChange.setApprovalStatus(ApprovalStatus.Pending);
//            dataChange.setInputerId(UserIdUtil.getUser());
//            dataChange.setInputDate(new Date());
//            dataChange.setAction(ChangeAction.Delete);
//            dataChange.setEntityId(reksadana.getCode());
//            dataChange.setTableName("comp_reksadana");
//            dataChange.setEntityClassName(Reksadana.class.getName());
//            dataChange.setDataBefore(jsonbefore);
//            dataChange.setDataChange(jsonAfter);
//            complianceDataChangeRepository.save(dataChange);
//
//            responseDto.setCode(HttpStatus.OK.toString());
//            responseDto.setMessage("OK");
//            responseDto.setPayload("Delete success where code " + code + "! Waiting for Approval!");
//        }catch (Exception e){
//            responseDto.setCode(HttpStatus.BAD_REQUEST.toString());
//            responseDto.setMessage("FAILED");
//            responseDto.setPayload(e.getMessage());
//        }
//
//        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
//    }

    public ResponseEntity<ResponseDTO> searchReksadana(String findByCode) {
        List<Reksadana> reksadanas = reksadanaRepository.searchByCodeLike(ApprovalStatus.Approved, findByCode);

        if (reksadanas.size() == 0) {
            reksadanas = reksadanaRepository.searchByNameLike(ApprovalStatus.Approved, findByCode);
            if (reksadanas.size() == 0) {
                reksadanas = reksadanaRepository.searchByExternalCodeLike(ApprovalStatus.Approved, findByCode);
            }
        }

        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("OK");
        responseDto.setPayload(reksadanas);

        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDTO> findAllReksadana() {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("OK");
        responseDto.setPayload(reksadanaRepository.findAllByDeleteAndApprovalStatus(false, ApprovalStatus.Approved));

        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
    }

    @Transactional
//    public ResponseEntity<ResponseDTO> insertReksadanaUpload(String param, List<Map<String, String>> reksadanaList) {
//        String message = "Input data success!";
//        ResponseDTO responseDto = new ResponseDTO();
//        List<Reksadana> newArrayReksadana = new ArrayList<>();
//        List<ComplianceDataChange> newComplianceDataChange = new ArrayList<>();
//        try {
//            System.out.println(param);
//            if (param.equalsIgnoreCase("new")) {
//                int row = 1;
//                for (Map<String, String> reksadana : reksadanaList) {
//                    row += 1;
//                    String reksadanaCode = reksadana.get("reksadanaCode").trim();
//                    String reksadanaName = reksadana.get("reksadanaName");
//                    String externalCode = reksadana.get("externalCode").trim();
//                    String address = reksadana.get("address");
//                    String email = reksadana.get("email");
//                    String manajerInvestasi = reksadana.get("manajerInvestasi");
//                    String pic = reksadana.get("pic");
//                    String reksadanaType = reksadana.get("reksadanaType");
//                    String convSyr = reksadana.get("conven/syariah");
//                    String tnabString = reksadana.get("tnabMinimum");
//                    String modal = reksadana.get("percentageModal");
//
//                    if (reksadanaCode == "" || reksadanaCode == null || reksadanaType == "" || reksadanaType == null ||
//                            convSyr == "" || convSyr == null || externalCode == "" || externalCode == null || convSyr == "" || convSyr == null ||
//                            tnabString == "" || tnabString == null || modal == "" || modal == null) {
//                        throw new Exception("Required properties are missing in Row: " + row);
//                    }
//                    Reksadana rd = reksadanaRepository.findByCodeAndDelete(reksadanaCode, false);
//                    if(rd != null){
//                        throw new Exception("Reksadana " + reksadanaCode + " has been regestered!");
//                    }
//
//                    boolean conventional = false;
//                    boolean syariah = true;
//                    if (convSyr.equalsIgnoreCase("conventional") || convSyr.equalsIgnoreCase("conven")) {
//                        conventional = true;
//                        syariah = false;
//                    }
//                    double tnabMin = Double.valueOf(tnabString);
//                    double percentageModal = Double.valueOf(modal);
//                    ReksadanaType rdType = reksadanaTypeRepository.findByReksadanaType(reksadanaType);
//                    if(rdType != null){
//                        rd = new Reksadana();
//                        rd.setApprovalStatus(ApprovalStatus.Pending);
//                        rd.setInputDate(new Date());
//                        rd.setInputerId(UserIdUtil.getUser());
//                        rd.setDelete(false);
//                        rd.setCode(reksadanaCode);
//                        rd.setName(reksadanaName);
//                        rd.setAddress(address);
//                        rd.setExternalCode(externalCode);
//                        rd.setEmail(email);
//                        rd.setPercentageModal(percentageModal);
//                        rd.setReksadanaType(rdType);
//                        rd.setTnabMinimum(tnabMin);
//                        rd.setManajerInvestasi(manajerInvestasi);
//                        rd.setPic(pic);
//                        rd.setSyariah(syariah);
//                        rd.setConventional(conventional);
//                    } else {
//                        throw new Exception("Reksadana type: " + rdType + " is not regestered in Row "+ row +"!");
//                    }
//                    newArrayReksadana.add(rd);
//                }
//                reksadanaRepository.saveAll(newArrayReksadana);
//                message = "Create New Reksadana Data Success!";
//            } else {
//                int row = 1;
//                for (Map<String, String> reksadana : reksadanaList) {
//                    row += 1;
//                    String reksadanaCode = reksadana.get("reksadanaCode").trim();
//                    Reksadana reksadanaBefore = new Reksadana();
//                    Reksadana reksadanaAfter = new Reksadana();
//
//                    System.out.println(reksadanaCode);
//                    if (reksadanaCode == "" || reksadanaCode == null || reksadanaCode.isEmpty()) {
//                        throw new Exception("Reksadana code are missing in Row "+ row +"!");
//                    } else {
//                        reksadanaBefore = reksadanaRepository.findByCode(reksadanaCode);
//                        if(reksadanaBefore == null){
//                            throw new Exception("This reksadana code: "+reksadanaCode+" is not registered!");
//                        }
//                        reksadanaAfter.setInputDate(new Date());
//                        reksadanaAfter.setInputerId(UserIdUtil.getUser());
//                        reksadanaAfter.setDelete(false);
//                    }
//
//                    String reksadanaName = reksadana.get("reksadanaName");
//                    if (reksadanaName != null || reksadanaName != "" || !reksadanaName.isEmpty()){
//                        reksadanaAfter.setName(reksadanaName);
//                    }else {
//                        reksadanaAfter.setName(reksadanaBefore.getName());
//                    }
//
//                    String externalCode = reksadana.get("externalCode").trim();
//                    if (externalCode != null || externalCode != "" || !externalCode.isEmpty()){
//                        reksadanaAfter.setExternalCode(externalCode);
//                    } else {
//                        reksadanaAfter.setExternalCode(reksadanaBefore.getName());
//                    }
//
//                    String address = reksadana.get("address");
//                    if (address != null || address != "" || !address.isEmpty()){
//                        reksadanaAfter.setAddress(address);
//                    } else {
//                        reksadanaAfter.setAddress(reksadanaBefore.getAddress());
//                    }
//
//                    String email = reksadana.get("email");
//                    if (email != null || email != "" || !email.isEmpty()){
//                        reksadanaAfter.setEmail(email);
//                    } else {
//                        reksadanaAfter.setAddress(reksadanaBefore.getAddress());
//                    }
//
//                    String manajerInvestasi = reksadana.get("manajerInvestasi");
//                    if (manajerInvestasi != null || manajerInvestasi != "" || !manajerInvestasi.isEmpty()){
//                        reksadanaAfter.setManajerInvestasi(manajerInvestasi);
//                    } else {
//                        reksadanaAfter.setManajerInvestasi(reksadanaBefore.getManajerInvestasi());
//                    }
//
//                    String pic = reksadana.get("pic");
//                    if (pic != null || pic != "" || !pic.isEmpty()){
//                        reksadanaAfter.setPic(pic);
//                    } else {
//                        reksadanaAfter.setPic(reksadanaBefore.getPic());
//                    }
//
//                    String reksadanaType = reksadana.get("reksadanaType");
//                    System.out.println(reksadanaType);
//                    if (reksadanaType == null ||  reksadanaType == "" || reksadanaType.isEmpty()){
//                        reksadanaAfter.setReksadanaType(reksadanaBefore.getReksadanaType());
//                    } else {
//                        ReksadanaType rdType = reksadanaTypeRepository.findByReksadanaType(reksadanaType);
//                        if(rdType == null){
//                            throw new Exception("Reksadana type: " + rdType + " is not regestered in Row "+ row +"!");
//                        } else {
//                            reksadanaAfter.setReksadanaType(rdType);
//                        }
//                    }
//
//                    String convSyr = reksadana.get("conven/syariah");
//                    if (convSyr != null || convSyr != "" || !convSyr.isEmpty()){
//                        boolean conventional = false;
//                        boolean syariah = true;
//                        if (convSyr.equalsIgnoreCase("conventional") || convSyr.equalsIgnoreCase("conven")) {
//                            conventional = true;
//                            syariah = false;
//                        }
//                        reksadanaAfter.setSyariah(syariah);
//                        reksadanaAfter.setConventional(conventional);
//                    } else {
//                        reksadanaAfter.setSyariah(reksadanaBefore.isSyariah());
//                        reksadanaAfter.setConventional(reksadanaBefore.isConventional());
//                    }
//
//                    String tnabString = reksadana.get("tnabMinimum");
//                    if (tnabString != null || tnabString != "" || !tnabString.isEmpty()){
//                        double tnabMin = Double.valueOf(tnabString);
//                        reksadanaAfter.setTnabMinimum(tnabMin);
//                    } else {
//                        reksadanaAfter.setTnabMinimum(reksadanaBefore.getTnabMinimum());
//                    }
//
//                    String modal = reksadana.get("percentageModal");
//                    if (modal != null || modal != "" || !modal.isEmpty()){
//                        double percentageModal = Double.valueOf(modal);
//                        reksadanaAfter.setPercentageModal(percentageModal);
//                    }  else {
//                        reksadanaAfter.setPercentageModal(reksadanaBefore.getPercentageModal());
//                    }
//
//                    ObjectMapper Obj = new ObjectMapper();
//                    String jsonbefore = Obj.writeValueAsString(reksadanaBefore);
//                    ObjectMapper ObjAfter = new ObjectMapper();
//                    String jsonAfter = ObjAfter.writeValueAsString(reksadanaAfter);
//
//                    ComplianceDataChange dataChange = new ComplianceDataChange();
//                    dataChange.setApprovalStatus(ApprovalStatus.Pending);
//                    dataChange.setInputerId(UserIdUtil.getUser());
//                    dataChange.setInputDate(new Date());
//                    dataChange.setAction(ChangeAction.Edit);
//                    dataChange.setEntityId(reksadanaBefore.getCode());
//                    dataChange.setTableName("comp_reksadana");
//                    dataChange.setEntityClassName(Reksadana.class.getName());
//                    dataChange.setDataBefore(jsonbefore);
//                    dataChange.setDataChange(jsonAfter);
//                    newComplianceDataChange.add(dataChange);
//                }
//
//                complianceDataChangeRepository.saveAll(newComplianceDataChange);
//                message = "Edit Reksadana Data Success!";
//            }
//            responseDto.setCode(HttpStatus.OK.toString());
//            responseDto.setMessage("OK");
//            responseDto.setPayload(message);
//
//        } catch (Exception e) {
//            responseDto.setCode(HttpStatus.BAD_REQUEST.toString());
//            responseDto.setMessage("FAILED");
//            responseDto.setPayload(e.getMessage());
//            e.printStackTrace();
//        }
//        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
//    }

    public ResponseEntity<ResponseDTO> allPendingDataReksadana() {
        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("OK");
        responseDto.setPayload(reksadanaRepository.searchPendingData());
        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDTO> approveDataReksadana(Map<String, List<String>> codeList) {
        String approverId = UserIdUtil.getUser();
        List<String> codes = codeList.get("idList");
        for (String code : codes){
            reksadanaRepository.approveOrRejectReksadana("Approved", new Date(), approverId, code);
        }

        ResponseDTO responseDto = new ResponseDTO();
        responseDto.setCode(HttpStatus.OK.value());
        responseDto.setMessage("OK");
        responseDto.setPayload("Data have approved!");
        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
    }

//    public ResponseEntity<ResponseDTO> rejectDataReksadana(Map<String, List<String>> codeList) {
//        ResponseDTO responseDto = new ResponseDTO();
//        String approverId = UserIdUtil.getUser();
//        List<String> codes = codeList.get("idList");
//        try {
//            for (String code : codes){
//                List<KINVReksadana> kinvReksadanaList = kinvReksadanaRepository.getAllByReksadanaCode(code);
//                if (kinvReksadanaList.size() > 0){
//                    throw new Exception("Please Reject KINV Reksadana That Related w/ This Code First!");
//                } else {
//                    Reksadana reksadana = reksadanaRepository.findByCode(code);
//                    reksadanaRepository.delete(reksadana);
//                }
//            }
//            responseDto.setCode(HttpStatus.OK.toString());
//            responseDto.setMessage("OK");
//            responseDto.setPayload("Data have rejected!");
//        }catch (Exception e){
//            responseDto.setCode(HttpStatus.BAD_REQUEST.toString());
//            responseDto.setMessage("FAILED");
//            responseDto.setPayload(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return new ResponseEntity<ResponseDTO>(responseDto, HttpStatus.OK);
//    }
}
