package com.services.billingservice.service.impl;

import com.services.billingservice.dto.request.CreateBillingCustomerRequest;
import com.services.billingservice.dto.request.CreateNasabahTransferAssetRequest;
import com.services.billingservice.dto.request.UpdateNasabahTransferAssetRequest;
import com.services.billingservice.dto.response.BillingCustomerDTO;
import com.services.billingservice.dto.response.NasabahTransferAssetDTO;
import com.services.billingservice.exception.DataNotFoundException;
import com.services.billingservice.model.BillingCustomer;
import com.services.billingservice.model.BillingNasabahTransferAsset;
import com.services.billingservice.repository.BillingCustomerRepository;
import com.services.billingservice.repository.BillingNasabahTransferAssetRepository;
import com.services.billingservice.service.BillingCustomerService;
import com.services.billingservice.service.BillingNasabahTransferAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingCustomerServiceImpl implements BillingCustomerService {

    private final BillingCustomerRepository billingCustomerRepository;

    @Override
    public BillingCustomerDTO create(CreateBillingCustomerRequest request) {
        String customerCode = request.getCustomerCode();
        String category = request.getCategory();
        String type = request.getType();
        String namaMI = request.getNamaMI();
        String alamatMI = request.getAlamatMI();
        String debitTransfer = request.getDebitTransfer();
        String account = request.getAccount();
        String accountName = request.getAccountName();
        String safekeepingFee = request.getSafekeepingFee();
        double vat = request.getVat();
        double kseiTransaction = request.getKseiTransaction();
        double bis4Transaction = request.getBis4Transaction();
        double transactionHandling = request.getTransactionHandling();
        double proxyServices = request.getProxyServices();
        double securitiesTransaction = request.getSecuritiesTransaction();
        double transactionFee = request.getTransactionFee();
        String glAccountHasil = request.getGlAccountHasil();
        String npwp = request.getNpwp();
        String nameNPWP = request.getNameNPWP();
        String costCenter = request.getCostCenter();
        double safekeepingKsei = request.getSafekeepingKsei();

        BillingCustomer billingCustomer = BillingCustomer.builder()
                .customerCode(customerCode)
                .category(category)
                .type(type)
                .namaMI(namaMI)
                .alamatMI(alamatMI)
                .debitTransfer(debitTransfer)
                .account(account)
                .accountName(accountName)
                .safekeepingFee(safekeepingFee)
                .vat(vat)
                .kseiTransaction(kseiTransaction)
                .bis4Transaction(bis4Transaction)
                .transactionHandling(transactionHandling)
                .proxyServices(proxyServices)
                .securitiesTransaction(securitiesTransaction)
                .transactionFee(transactionFee)
                .glAccountHasil(glAccountHasil)
                .npwp(npwp)
                .nameNPWP(nameNPWP)
                .costCenter(costCenter)
                .safekeepingKsei(safekeepingKsei)
                .build();


        BillingCustomer dataSaved = billingCustomerRepository.save(billingCustomer);
        return mapToDTO(dataSaved);
    }

    @Override
    public List<BillingCustomerDTO> upload(List<CreateBillingCustomerRequest> request) {
        List<BillingCustomer> billingCustomers = mapTopCustomerList(request);
        List<BillingCustomer> billingCustomers1 = billingCustomerRepository.saveAll(billingCustomers);
        return mapCustomerDTOList(billingCustomers1);
    }

    private BillingCustomerDTO mapTOCustomerDTO(BillingCustomer customer) {
        return BillingCustomerDTO.builder()
                .id(String.valueOf(customer.getId()))
                .customerCode(customer.getCustomerCode())
                .category(customer.getCategory())
                .type(customer.getType())
                .namaMI(customer.getNamaMI())
                .alamatMI(customer.getAlamatMI())
                .debitTransfer(customer.getDebitTransfer())
                .account(customer.getAccount())
                .accountName(customer.getAccountName())
                .safekeepingFee(customer.getSafekeepingFee())
                .vat(customer.getVat())
                .kseiTransaction(customer.getKseiTransaction())
                .bis4Transaction(customer.getBis4Transaction())
                .transactionHandling(customer.getTransactionHandling())
                .proxyServices(customer.getProxyServices())
                .securitiesTransaction(customer.getSecuritiesTransaction())
                .transactionFee(customer.getTransactionFee())
                .glAccountHasil(customer.getGlAccountHasil())
                .npwp(customer.getNpwp())
                .nameNPWP(customer.getNpwp())
                .costCenter(customer.getCostCenter())
                .safekeepingKsei(customer.getSafekeepingKsei())
                .build(); }

    private List<BillingCustomerDTO> mapCustomerDTOList(List<BillingCustomer> customerList) {
        return customerList.stream()
                .map(this::mapTOCustomerDTO)
                .collect(Collectors.toList());
    }

    private BillingCustomer mapToBillCustomer(CreateBillingCustomerRequest request) {
        return BillingCustomer.builder()
                .customerCode(request.getCustomerCode())
                .category(request.getCategory())
                .type(request.getType())
                .namaMI(request.getNamaMI())
                .alamatMI(request.getAlamatMI())
                .debitTransfer(request.getDebitTransfer())
                .account(request.getAccount())
                .accountName(request.getAccountName())
                .safekeepingFee(request.getSafekeepingFee())
                .vat(request.getVat())
                .kseiTransaction(request.getKseiTransaction())
                .bis4Transaction(request.getBis4Transaction())
                .transactionHandling(request.getTransactionHandling())
                .proxyServices(request.getProxyServices())
                .securitiesTransaction(request.getSecuritiesTransaction())
                .transactionFee(request.getTransactionFee())
                .glAccountHasil(request.getGlAccountHasil())
                .npwp(request.getNpwp())
                .nameNPWP(request.getNameNPWP())
                .costCenter(request.getCostCenter())
                .safekeepingKsei(request.getSafekeepingKsei())
                .build();
    }

    private List<BillingCustomer> mapTopCustomerList(List<CreateBillingCustomerRequest> requests) {
        return requests.stream()
                .map(this::mapToBillCustomer)
                .collect(Collectors.toList());
    }

    private BillingCustomerDTO mapToDTO(BillingCustomer billingCustomer) {
        return BillingCustomerDTO.builder().build().builder()
                .id(String.valueOf(billingCustomer.getId()))
                .customerCode(billingCustomer.getCustomerCode())
                .category(billingCustomer.getCategory())
                .type(billingCustomer.getType())
                .namaMI(billingCustomer.getNamaMI())
                .alamatMI(billingCustomer.getAlamatMI())
                .debitTransfer(billingCustomer.getDebitTransfer())
                .account(billingCustomer.getAccount())
                .accountName(billingCustomer.getAccountName())
                .safekeepingFee(billingCustomer.getSafekeepingFee())
                .vat(billingCustomer.getVat())
                .kseiTransaction(billingCustomer.getKseiTransaction())
                .bis4Transaction(billingCustomer.getBis4Transaction())
                .transactionHandling(billingCustomer.getTransactionHandling())
                .proxyServices(billingCustomer.getProxyServices())
                .securitiesTransaction(billingCustomer.getSecuritiesTransaction())
                .transactionFee(billingCustomer.getTransactionFee())
                .glAccountHasil(billingCustomer.getGlAccountHasil())
                .npwp(billingCustomer.getNpwp())
                .nameNPWP(billingCustomer.getNpwp())
                .costCenter(billingCustomer.getCostCenter())
                .safekeepingKsei(billingCustomer.getSafekeepingKsei())
                .build();
    }


}
