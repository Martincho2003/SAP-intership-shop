package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.CategoryAlreadyExistsError;
import com.example.sap_shop.error.CategoryNotFoundException;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService{
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductService productService) {
        this.categoryRepository = categoryRepository;
        this.productService = productService;
    }


    @Transactional
    public void createCategory(CategoryDTO categoryDTO) throws CategoryAlreadyExistsError, FieldCannotBeEmptyException {
        if (categoryRepository.findByName(categoryDTO.getName()) != null) {
            throw new CategoryAlreadyExistsError("Category already exist.");
        }

        Category category = new Category();
        if (categoryDTO.getName() != null) {
            category.setName(categoryDTO.getName());
            categoryRepository.save(category);
        } else {
            throw new FieldCannotBeEmptyException("Field can not be empty!");
        }
    }


    @Transactional
    public void deleteCategory(String name) throws CategoryNotFoundException {

        if (categoryRepository.findByName(name) == null) {
            throw new CategoryNotFoundException("Category with name " + name + " doesn't exist.");
        }

        categoryRepository.deleteByName(name);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(productService::convertCategoryToDTO) // Use the simplified method
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> findByNameContainingIgnoreCase(String name) {
        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(name);
        return categories.stream()
                .map(productService::convertCategoryToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProductsFromCategory(String category) {
        return productService.getAllProductsFromCategory(category);

    }
}
