package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.error.CategoryAlreadyExistsError;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService{
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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


    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(category.getName());
    }
}
