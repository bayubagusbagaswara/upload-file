package com.services.billingservice.service.mock;

import com.services.billingservice.dto.mock.MockKycCustomerDTO;
import com.services.billingservice.model.MockKycCustomer;
import com.services.billingservice.repository.MockKycCustomerRepository;
import com.services.billingservice.utils.ConvertBigDecimalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockKycCustomerServiceImpl implements MockKycCustomerService {

    private final MockKycCustomerRepository mockKycCustomerRepository;

    @Override
    public String create() {

        List<MockKycCustomerDTO> sampleData = createSampleData();
        for (int i = 0; i < sampleData.size(); i++) {
            MockKycCustomerDTO dto = sampleData.get(i);
            log.info("Aid : {}", dto.getAid());
        }

        log.info("Sample Data size : {}", sampleData.size());

        for (MockKycCustomerDTO dto : sampleData) {
            log.info("[DTO] AID: {}", dto.getAid());
            log.info("[DTO] Customer Fee: {}", dto.getCustomerFee());

            String kseiSafeCode = dto.getKseiSafeCode().isEmpty() ? "" : dto.getKseiSafeCode();
            String journal = dto.getJournal().isEmpty() ? "" : dto.getJournal();
            String billingCategory = dto.getBillingCategory().isEmpty() ? "" : dto.getBillingCategory();
            String billingType = dto.getBillingType().isEmpty() ? "" : dto.getBillingType();
            String billingTemplate = dto.getBillingTemplate().isEmpty() ? "" : dto.getBillingTemplate();

            BigDecimal minimumFee = ConvertBigDecimalUtil.parseBigDecimalOrDefault(dto.getMinimumFee());
            double customerFee = dto.getCustomerFee().isEmpty() ? 0 : Double.parseDouble(dto.getCustomerFee());

            MockKycCustomer mockKycCustomer = MockKycCustomer.builder()
                    .aid(dto.getAid())
                    .kseiSafeCode(kseiSafeCode)
                    .minimumFee(minimumFee)
                    .customerFee(customerFee)
                    .journal(journal)
                    .billingCategory(billingCategory)
                    .billingType(billingType)
                    .billingTemplate(billingTemplate)
                    .build();

            MockKycCustomer save = mockKycCustomerRepository.save(mockKycCustomer);
            log.info("ID : {}", save.getId());
        }

        return "Success created data mock customer";
    }

    @Override
    public List<MockKycCustomerDTO> getAll() {
        List<MockKycCustomer> mockKycCustomerList = mockKycCustomerRepository.findAll();
        return mapToDTOList(mockKycCustomerList);
    }

    @Override
    public List<MockKycCustomerDTO> getByAid(String aid) {
        List<MockKycCustomer> mockKycCustomerList = mockKycCustomerRepository.findByAid(aid);
        return mapToDTOList(mockKycCustomerList);
    }

    @Override
    public List<MockKycCustomerDTO> getAllByBillingCategoryAndBillingType(String billingCategory, String billingType) {
        List<MockKycCustomer> mockKycCustomerList = mockKycCustomerRepository.findAllByBillingCategoryAndBillingType(billingCategory, billingType);
        return mapToDTOList(mockKycCustomerList);
    }

    @Override
    public String deleteAll() {
        try {
            mockKycCustomerRepository.deleteAll();
            return "Successfully deleted all Mock Kyc Customer";
        } catch (Exception e) {
            log.error("Error when delete all Mock Kyc Customer : " + e.getMessage());
            throw new RuntimeException("Error when delete all Mock Kyc Customer");
        }
    }

    private static MockKycCustomer mapToModel(MockKycCustomerDTO dto) {
        return MockKycCustomer.builder()
                .aid(dto.getAid())
                .kseiSafeCode(dto.getKseiSafeCode())
                .minimumFee(new BigDecimal(dto.getMinimumFee()))
                .customerFee(Double.parseDouble(dto.getCustomerFee()))
                .journal(dto.getJournal())
                .billingCategory(dto.getBillingCategory())
                .billingType(dto.getBillingType())
                .billingTemplate(dto.getBillingTemplate())
                .build();
    }

    private static List<MockKycCustomer> mapToModelList(List<MockKycCustomerDTO> dtoList) {
        return dtoList.stream()
                .map(MockKycCustomerServiceImpl::mapToModel)
                .collect(Collectors.toList());
    }

    private static MockKycCustomerDTO mapToDTO(MockKycCustomer mockKycCustomer) {
        return MockKycCustomerDTO.builder()
                .id(String.valueOf(mockKycCustomer.getId()))
                .aid(mockKycCustomer.getAid())
                .kseiSafeCode(mockKycCustomer.getKseiSafeCode())
                .minimumFee(String.valueOf(mockKycCustomer.getMinimumFee()))
                .customerFee(String.valueOf(mockKycCustomer.getCustomerFee()))
                .journal(mockKycCustomer.getJournal())
                .billingCategory(mockKycCustomer.getBillingCategory())
                .billingType(mockKycCustomer.getBillingType())
                .billingTemplate(mockKycCustomer.getBillingTemplate())
                .build();
    }

    private static List<MockKycCustomerDTO> mapToDTOList(List<MockKycCustomer> mockKycCustomerList) {
        return mockKycCustomerList.stream()
                .map(MockKycCustomerServiceImpl::mapToDTO)
                .collect(Collectors.toList());
    }

    private static List<MockKycCustomerDTO> createSampleData() {
        List<MockKycCustomerDTO> mockCustomerList = new ArrayList<>();

        mockCustomerList.add(categoryCoreType1());
        mockCustomerList.add(categoryCoreType2());
        mockCustomerList.add(categoryCoreType3());
        mockCustomerList.add(categoryCoreType4A());
        mockCustomerList.add(categoryCoreType4B());
        mockCustomerList.add(categoryCoreType5());
        mockCustomerList.add(categoryCoreType6());
        mockCustomerList.add(categoryCoreType7Mufg());
        mockCustomerList.add(categoryCoreType7Gudh());
        mockCustomerList.add(categoryCoreType8AlamManunggal());
        mockCustomerList.add(categoryCoreType8IndoInfrastruktur());
        mockCustomerList.add(categoryCoreType8MandalaKapital());
        mockCustomerList.add(categoryCoreType9());
        mockCustomerList.add(categoryCoreType10());
        mockCustomerList.add(categoryCoreType11Visiku());
        mockCustomerList.add(categoryCoreType11Fulus());
        mockCustomerList.add(categoryCoreType11Frtaec());

        return mockCustomerList;
    }

    private static MockKycCustomerDTO categoryCoreType1() {
        return MockKycCustomerDTO.builder()
                .aid("15PCAP")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_1")
                .billingTemplate("TEMPLATE_1")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType2() {
        return MockKycCustomerDTO.builder()
                .aid("16NUII")
                .kseiSafeCode("")
                .minimumFee("500000")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_2")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType3() {
        return MockKycCustomerDTO.builder()
                .aid("14GIGC")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("GL 713017 CC 9207")
                .billingCategory("CORE")
                .billingType("TYPE_3")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType4A() {
        // ITAMA = TEMPLATE_3
        return MockKycCustomerDTO.builder()
                .aid("17OBAL")
                .kseiSafeCode("BDMN2OBAL00119")
                .minimumFee("")
                .customerFee("0.03")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_4")
                .billingTemplate("TEMPLATE_3")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType4B() {
        // EB = TEMPLATE_5
        return MockKycCustomerDTO.builder()
                .aid("17OBAL")
                .kseiSafeCode("BDMN2OBAL00119")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_4")
                .billingTemplate("TEMPLATE_5")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType5() {
        return MockKycCustomerDTO.builder()
                .aid("14ZDEY")
                .kseiSafeCode("BDMN2ZDEY00134")
                .minimumFee("")
                .customerFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_5")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType6() {
        return MockKycCustomerDTO.builder()
                .aid("14AJUT")
                .kseiSafeCode("BDMN2AJUT00157")
                .minimumFee("")
                .customerFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_6")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType7Mufg() {
        return MockKycCustomerDTO.builder()
                .aid("12MUFG")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_7")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType7Gudh() {
        return MockKycCustomerDTO.builder()
                .aid("17GUDH")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_7")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType8AlamManunggal() {
        return MockKycCustomerDTO.builder()
                .aid("ALMAN")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType8IndoInfrastruktur() {
        return MockKycCustomerDTO.builder()
                .aid("INFRAS")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType8MandalaKapital() {
        return MockKycCustomerDTO.builder()
                .aid("MANKAP")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_8")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType9() {
        return MockKycCustomerDTO.builder()
                .aid("13KONI")
                .kseiSafeCode("BDMN2KONI00111")
                .minimumFee("")
                .customerFee("0.05")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_9")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType10() {
        return MockKycCustomerDTO.builder()
                .aid("00N0IC")
                .kseiSafeCode("")
                .minimumFee("")
                .customerFee("0.02")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_10")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType11Visiku() {
        return MockKycCustomerDTO.builder()
                .aid("VISIKU")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType11Fulus() {
        return MockKycCustomerDTO.builder()
                .aid("FULUS")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .billingTemplate("")
                .build();
    }

    private static MockKycCustomerDTO categoryCoreType11Frtaec()  {
        return MockKycCustomerDTO.builder()
                .aid("FRTAEC")
                .kseiSafeCode("")
                .minimumFee("5000000")
                .customerFee("0.35")
                .journal("")
                .billingCategory("CORE")
                .billingType("TYPE_11")
                .billingTemplate("")
                .build();
    }
}
