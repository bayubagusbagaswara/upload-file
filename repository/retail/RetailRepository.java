package com.services.billingservice.repository.retail;

import com.services.billingservice.dto.retail.RetailType1IdrDTO;
import com.services.billingservice.dto.retail.RetailType1UsdDTO;

import java.util.List;

/**
 * declare method contract
 */
public interface RetailRepository {

    List<RetailType1IdrDTO> getAllRetailType1IDR();

    List<RetailType1UsdDTO> getAllRetailType1USD();

}
