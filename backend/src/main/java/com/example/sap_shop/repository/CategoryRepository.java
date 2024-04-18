package com.example.sap_shop.repository;

import com.example.sap_shop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

    void deleteByName(String name);

    List<Category> findByNameContainingIgnoreCase(String name);
}
