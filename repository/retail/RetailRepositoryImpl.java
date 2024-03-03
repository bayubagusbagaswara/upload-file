package com.services.billingservice.repository.retail;

import com.services.billingservice.dto.retail.RetailType1IdrDTO;
import com.services.billingservice.dto.retail.RetailType1UsdDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class RetailRepositoryImpl implements RetailRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<RetailType1IdrDTO> getAllRetailType1IDR() {
        // Implementasi pencarian kustom menggunakan EntityManager
        // Misalnya, menggunakan JPQL
        String nativeQuery = "SELECT * FROM";

        List<Object[]> resultList = entityManager.createNativeQuery(nativeQuery)
                .getResultList();

        for (int i = 0; i < resultList.size(); i++) {
            Object[] objects = resultList.get(i);
            log.info("Object - " + i + " is {}",  Arrays.stream(objects).collect(Collectors.toList()));
        }

        // Mapping hasil query ke DTO, pastikan urutannya sesuai
//        return resultList.stream()
//                .map(row -> new UserDTO(
//                        ((Number) row[0]).intValue(),  // Assuming id is of type INT
//                        (String) row[1],
//                        ((Number) row[2]).intValue()   // Assuming age is of type INT
//                ))
//                .toList();

        return null;
    }

    @Override
    public List<RetailType1UsdDTO> getAllRetailType1USD() {
        return null;
    }
}
