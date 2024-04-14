package com.example.sap_shop.repository;

import com.example.sap_shop.model.Discount;
import com.example.sap_shop.model.Sale;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface SaleRepository extends CrudRepository<Sale, Long> {
    List<Sale> findByEndDate(Date endDate);
    List<Sale> findByStartDate(Date startDate);
}
