package com.example.sap_shop.controller;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.error.CategoryAlreadyExistsError;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> addNewCategory (@RequestBody CategoryDTO categoryDTO) {
        try {
            categoryService.createCategory(categoryDTO);
        } catch (FieldCannotBeEmptyException e) {
            return ResponseEntity.status(409).body("Field can not be empty!");
        } catch (CategoryAlreadyExistsError e) {
            return ResponseEntity.status(409).body("Category already exists!");
        }
        return ResponseEntity.ok().body("Category created successfully.");
    }
}
