package com.example.sap_shop.controller;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.CategoryAlreadyExistsError;
import com.example.sap_shop.error.CategoryNotFoundException;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.ProductAlreadyExistException;
import com.example.sap_shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //works
    @PostMapping("/create")
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

    //works
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCategory(@RequestParam String name) {
        List<CategoryDTO> categories = categoryService.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(categories);
    }


    @GetMapping("/search/{categoryName}")
    public ResponseEntity<?> searchProductsByCategory(@PathVariable String categoryName) {
        List<ProductDTO> products = categoryService.getAllProductsFromCategory(categoryName);
        return ResponseEntity.ok(products);
    }


    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteCategory(@PathVariable String name){
        try {
            categoryService.deleteCategory(name);
            return ResponseEntity.ok().body("Category deleted successfully.");
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}
