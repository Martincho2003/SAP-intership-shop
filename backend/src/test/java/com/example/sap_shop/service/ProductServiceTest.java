package com.example.sap_shop.service;

import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.CategoryNotFoundException;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.ProductAlreadyExistException;
import com.example.sap_shop.error.ProductNotFoundException;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.repository.CategoryRepository;
import com.example.sap_shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    void createProduct_Successfully() throws FieldCannotBeEmptyException, CategoryNotFoundException, ProductAlreadyExistException {
        ProductDTO productDTO = new ProductDTO("TestProduct", "Description", 100.0f, 90.0f, 50, "/img/path", 10.0f, "TestCategory");
        when(productRepository.findByName("TestProduct")).thenReturn(null);
        when(categoryRepository.findByName("TestCategory")).thenReturn(new Category());

        productService.createProduct(productDTO);

        verify(productRepository).save(productCaptor.capture());
        assertEquals("TestProduct", productCaptor.getValue().getName());
    }

    @Test
    void createProduct_ThrowsProductAlreadyExistException() {
        ProductDTO productDTO = new ProductDTO("TestProduct", "Description", 100.0f, 90.0f, 50, "/img/path",10.0f,  "TestCategory");
        when(productRepository.findByName("TestProduct")).thenReturn(new Product());

        assertThrows(ProductAlreadyExistException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void createProduct_ThrowsFieldCannotBeEmptyException() {
        ProductDTO productDTO = new ProductDTO(null, null, null, null, null, null, null, null);

        assertThrows(FieldCannotBeEmptyException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void createProduct_ThrowsCategoryNotFoundException() {
        ProductDTO productDTO = new ProductDTO("TestProduct", "Description", 100.0f, 90.0f, 50,  "/img/path", 10.0f, "NonexistentCategory");
        when(productRepository.findByName("TestProduct")).thenReturn(null);
        when(categoryRepository.findByName("NonexistentCategory")).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void updateCategory_Successfully() throws CategoryNotFoundException, ProductNotFoundException {
        when(productRepository.findByName("TestProduct")).thenReturn(new Product());
        when(categoryRepository.findByName("TestCategory")).thenReturn(new Category());

        productService.updateCategory("TestProduct", "TestCategory");

        verify(productRepository).save(productCaptor.capture());
        assertNotNull(productCaptor.getValue().getCategory());
    }

    @Test
    void updateCategory_ThrowsProductNotFoundException() {
        when(productRepository.findByName("NonexistentProduct")).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.updateCategory("NonexistentProduct", "TestCategory"));
    }

    @Test
    void updateCategory_ThrowsCategoryNotFoundException() {
        when(productRepository.findByName("TestProduct")).thenReturn(new Product());
        when(categoryRepository.findByName("NonexistentCategory")).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> productService.updateCategory("TestProduct", "NonexistentCategory"));
    }

    @Test
    void deleteProduct_Successfully() throws ProductNotFoundException {
        when(productRepository.findByName("TestProduct")).thenReturn(new Product());

        productService.deleteProduct("TestProduct");

        verify(productRepository).deleteByName("TestProduct");
    }

    @Test
    void deleteProduct_ThrowsProductNotFoundException() {
        when(productRepository.findByName("NonexistentProduct")).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct("NonexistentProduct"));
    }

    @Test
    void getAllProductsFromCategory_ReturnsProducts() {
        Product mockProduct = new Product();
        mockProduct.setName("TestProduct");
        mockProduct.setDescription("TestDescription");
        mockProduct.setPrice(100.0f);
        mockProduct.setDiscountPrice(90.0f);
        mockProduct.setMinPrice(50.0f);
        mockProduct.setQuantity(10);
        mockProduct.setImagePath("/img/path");

        Category mockCategory = new Category();
        mockCategory.setName("TestCategory");
        mockProduct.setCategory(mockCategory);

        when(productRepository.findByCategoryName("TestCategory")).thenReturn(Arrays.asList(mockProduct));

        List<ProductDTO> products = productService.getAllProductsFromCategory("TestCategory");

        assertFalse(products.isEmpty());
        assertEquals("TestProduct", products.get(0).getName());
        assertEquals("TestCategory", products.get(0).getCategoryName());
    }

    @Test
    void getAllProducts_ReturnsAllProducts() {
        // Setup mock products
        Product mockProduct1 = new Product();
        mockProduct1.setName("Product1");
        mockProduct1.setDescription("Description1");
        mockProduct1.setPrice(100.0f);
        mockProduct1.setDiscountPrice(90.0f);
        mockProduct1.setMinPrice(80.0f);
        mockProduct1.setQuantity(50);
        mockProduct1.setImagePath("/img/product1.jpg");
        Category category1 = new Category();
        category1.setName("Category1");
        mockProduct1.setCategory(category1);

        Product mockProduct2 = new Product();
        mockProduct2.setName("Product2");
        mockProduct2.setDescription("Description2");
        mockProduct2.setPrice(200.0f);
        mockProduct2.setDiscountPrice(180.0f);
        mockProduct2.setMinPrice(160.0f);
        mockProduct2.setQuantity(30);
        mockProduct2.setImagePath("/img/product2.jpg");
        Category category2 = new Category();
        category2.setName("Category2");
        mockProduct2.setCategory(category2);

        when(productRepository.findAll()).thenReturn(Arrays.asList(mockProduct1, mockProduct2));

        // Execute the method
        List<ProductDTO> products = productService.getAllProducts();

        // Assertions
        assertEquals(2, products.size());
        assertEquals("Product1", products.get(0).getName());
        assertEquals("Category1", products.get(0).getCategoryName());
        assertEquals("Product2", products.get(1).getName());
        assertEquals("Category2", products.get(1).getCategoryName());
    }

    @Test
    void findByNameContainingIgnoreCase_FindsMatchingProducts() {
        // Setup mock products
        Product mockProduct1 = new Product();
        mockProduct1.setName("Coffee Maker");
        mockProduct1.setDescription("Makes great coffee");
        mockProduct1.setPrice(100.0f);
        mockProduct1.setDiscountPrice(90.0f);
        mockProduct1.setMinPrice(80.0f);
        mockProduct1.setQuantity(10);
        mockProduct1.setImagePath("/img/coffee_maker.jpg");
        Category category1 = new Category();
        category1.setName("Appliances");
        mockProduct1.setCategory(category1);

        Product mockProduct2 = new Product();
        mockProduct2.setName("Espresso Machine");
        mockProduct2.setDescription("Barista-grade espresso");
        mockProduct2.setPrice(300.0f);
        mockProduct2.setDiscountPrice(280.0f);
        mockProduct2.setMinPrice(250.0f);
        mockProduct2.setQuantity(5);
        mockProduct2.setImagePath("/img/espresso_machine.jpg");
        mockProduct2.setCategory(category1);

        when(productRepository.findByNameContainingIgnoreCase("coffee")).thenReturn(Arrays.asList(mockProduct1));

        // Execute the method
        List<ProductDTO> products = productService.findByNameContainingIgnoreCase("coffee");

        // Assertions
        assertEquals(1, products.size());
        assertEquals("Coffee Maker", products.get(0).getName());
        assertEquals("Appliances", products.get(0).getCategoryName());
    }
}
