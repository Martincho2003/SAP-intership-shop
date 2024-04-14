package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<String> getAllCategories(){
        List<String> categories = new ArrayList<>();
        categoryRepository.findAll().forEach((category -> {categories.add(category.getName());}));
        return categories;
    }
}
