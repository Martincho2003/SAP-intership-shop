package com.example.sap_shop.repository;

import com.example.sap_shop.model.Discount;
import com.example.sap_shop.model.Sale;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface DiscountRepository extends CrudRepository<Discount, Long> {
    List<Discount> findByEndDate(Date endDate);
    List<Discount> findByStartDate(Date startDate);
    Discount findByName(String name);
    List<Discount> findByNameContainingIgnoreCase(String name);
}
