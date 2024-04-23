package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.CategoryAlreadyExistsError;
import com.example.sap_shop.error.CategoryNotFoundException;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @Test
    void createCategory_Successfully() throws CategoryAlreadyExistsError, FieldCannotBeEmptyException {
        CategoryDTO categoryDTO = new CategoryDTO("Electronics", null);
        when(categoryRepository.findByName("Electronics")).thenReturn(null);

        categoryService.createCategory(categoryDTO);

        verify(categoryRepository).save(categoryCaptor.capture());
        assertEquals("Electronics", categoryCaptor.getValue().getName());
    }

    @Test
    void createCategory_ThrowsCategoryAlreadyExistsError() {
        CategoryDTO categoryDTO = new CategoryDTO("Electronics", null);
        when(categoryRepository.findByName("Electronics")).thenReturn(new Category());

        assertThrows(CategoryAlreadyExistsError.class, () -> categoryService.createCategory(categoryDTO));
    }

    @Test
    void createCategory_ThrowsFieldCannotBeEmptyException() {
        CategoryDTO categoryDTO = new CategoryDTO("", null);

        assertThrows(FieldCannotBeEmptyException.class, () -> categoryService.createCategory(categoryDTO));
    }

    @Test
    void deleteCategory_Successfully() throws CategoryNotFoundException, InvalidRequestBodyException {
        when(categoryRepository.findByName("Electronics")).thenReturn(new Category());

        categoryService.deleteCategory("Electronics");

        verify(categoryRepository).deleteByName("Electronics");
    }

    @Test
    void deleteCategory_ThrowsCategoryNotFoundException() {
        when(categoryRepository.findByName("Electronics")).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory("Electronics"));
    }

    @Test
    void deleteCategory_ThrowsInvalidRequestBodyException() {
        assertThrows(InvalidRequestBodyException.class, () -> categoryService.deleteCategory(""));
    }

    @Test
    void getAllCategories_ReturnsAllCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.findAll()).thenReturn(categories);
        when(productService.convertCategoryToDTO(any())).thenAnswer(invocation -> new CategoryDTO("Name", null));

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
    }

    @Test
    void findByNameContainingIgnoreCase_FindsCategories() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        when(categoryRepository.findByNameContainingIgnoreCase("elec")).thenReturn(categories);
        when(productService.convertCategoryToDTO(any())).thenAnswer(invocation -> new CategoryDTO("Electronics", null));

        List<CategoryDTO> result = categoryService.findByNameContainingIgnoreCase("elec");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.getName().contains("Electronics")));
    }

    @Test
    void getAllProductsFromCategory_ReturnsProducts() {
        when(productService.getAllProductsFromCategory("Electronics")).thenReturn(Arrays.asList(new ProductDTO(), new ProductDTO()));

        List<ProductDTO> products = categoryService.getAllProductsFromCategory("Electronics");

        assertEquals(2, products.size());
    }
}
