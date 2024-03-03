package com.services.billingservice.service;

public interface RetailType1Service {

    // parameter : category, type, monthYear

    // Filter by currency untuk memisahkan data dengan Billing yg Retail IDR dan Retail USD

    void calculate(String category, String type, String monthYear);

}
