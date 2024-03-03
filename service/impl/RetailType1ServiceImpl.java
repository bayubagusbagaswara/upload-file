package com.services.billingservice.service.impl;

import com.services.billingservice.dto.retail.RetailType1IdrDTO;
import com.services.billingservice.dto.retail.RetailType1UsdDTO;
import com.services.billingservice.service.RetailType1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RetailType1ServiceImpl implements RetailType1Service {

    // butuh data dari Account Balance

    @Override
    public void calculate(String category, String type, String monthYear) {

        // gak perlu pengecekan bulan dan tahun

        // hasilnya kita create 2 object untuk Retail USD dan Retail IDR

        // panggil semua data account balance untuk period sesuai MonthYear

        // balikannya adalah response semua data ST..., FR...., SBR....

        // pisahkan data berdasarkan IDR dan USD

        // masukkan data IDR ke method calculateRetailIDR

        // masukkan data USD ke method calculateRetailUSD
    }

    private static RetailType1IdrDTO calculateRetailIDR() {

        // create method untuk memisahkan masing-masing security berdasarkan group

        // method 1 untuk group yg ST

        return RetailType1IdrDTO.builder()
                .currency("IDR")
                .build();
    }

    private static RetailType1UsdDTO calculateRetailUSD() {

        return RetailType1UsdDTO.builder()
                .currency("USD")
                .build();
    }


}
