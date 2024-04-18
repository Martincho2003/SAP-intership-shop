package com.example.sap_shop.repository;

import com.example.sap_shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);
    Product findByName(String name);

    void deleteByName(String name);

    List<Product> findByCategoryName(String categoryName);
}
