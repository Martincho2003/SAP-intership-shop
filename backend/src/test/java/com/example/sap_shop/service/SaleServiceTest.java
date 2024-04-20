package com.example.sap_shop.service;

import com.example.sap_shop.dto.CategoryDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.SaleDto;
import com.example.sap_shop.error.*;
import com.example.sap_shop.model.Category;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.model.Sale;
import com.example.sap_shop.repository.CategoryRepository;
import com.example.sap_shop.repository.ProductRepository;
import com.example.sap_shop.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SaleServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private SaleRepository saleRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Captor
    private ArgumentCaptor<Sale> saleArgumentCaptor;

    @Autowired
    private SaleService saleService;

    @Test
    void createSaleSuccessfully() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("End of Year Sale");
        saleDto.setPercentage(20);
        saleDto.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day from now
        saleDto.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");
        ProductDTO productDTO = new ProductDTO("Laptop", 10, 999.99f, "Latest model");
        categoryDTO.setProductDTOS(List.of(productDTO));
        saleDto.setCategoryDTOS(List.of(categoryDTO));

        Product product = new Product();
        product.setName("Laptop");
        product.setQuantity(10);
        product.setPrice(999.99f);
        product.setDescription("Latest model");
        when(productRepository.findByName(productDTO.getName())).thenReturn(product);

        try {
            saleService.createSale(saleDto);
        } catch (InvalidRequestBodyException | ProductNotFoundException e) {
            e.printStackTrace();
        }

        verify(saleRepository, times(1)).save(saleArgumentCaptor.capture());

        Sale savedSale = saleArgumentCaptor.getValue();
        assertEquals(savedSale.getName(), saleDto.getName());
        assertNotNull(savedSale.getCategories());
        assertEquals(savedSale.getCategories().get(0).getName(), categoryDTO.getName());
        assertEquals(savedSale.getCategories().get(0).getProducts().get(0).getName(), productDTO.getName());
    }

    @Test
    void createSaleWithNoCategories() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Spring Sale");
        saleDto.setPercentage(15);
        saleDto.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day from now
        saleDto.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now

        try {
            saleService.createSale(saleDto);
        } catch (InvalidRequestBodyException | ProductNotFoundException e) {
            e.printStackTrace();
        }
        assertThrows(InvalidRequestBodyException.class, () -> saleService.createSale(saleDto), "Categories for sale are not set.");
    }

    @Test
    void createSaleWithNonExistentProduct() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Winter Sale");
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Home Appliances");
        ProductDTO productDTO = new ProductDTO("Heater", 10, 199.9f, "Keeps you warm");
        categoryDTO.setProductDTOS(List.of(productDTO));
        saleDto.setCategoryDTOS(List.of(categoryDTO));

        when(productRepository.findByName(productDTO.getName())).thenReturn(null);

        assertThrows(InvalidRequestBodyException.class, () -> saleService.createSale(saleDto), "Product with name Heater not found!");
    }

    @Test
    void updateSaleSettingsSuccessfully() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Spring Sale");
        saleDto.setPercentage(10);
        saleDto.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day from now
        saleDto.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now

        Sale sale = new Sale();
        sale.setName("Spring Sale");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(sale);

        try {
            saleService.updateSaleSettings(saleDto);
        } catch (InvalidRequestBodyException | FieldCannotBeEmptyException e) {
            e.printStackTrace();
        }

        verify(saleRepository, times(1)).save(saleArgumentCaptor.capture());

        Sale savedSale = saleArgumentCaptor.getValue();

        assertEquals(saleDto.getPercentage(), savedSale.getPercentage());
        assertEquals(saleDto.getStartDate(), savedSale.getStartDate());
        assertEquals(saleDto.getEndDate(), savedSale.getEndDate());
    }

    @Test
    void updateSaleSettingsThrowsInvalidRequestBodyExceptionForNegativePercentage() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Summer Sale");
        saleDto.setPercentage(-5); // Invalid percentage

        Sale sale = new Sale();
        sale.setName("Summer Sale");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(sale);

        assertThrows(InvalidRequestBodyException.class, () -> saleService.updateSaleSettings(saleDto),
                "Sale can't be under 0 percent.");
    }

    @Test
    void updateSaleSettingsThrowsInvalidRequestBodyExceptionForPastStartDate() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Winter Sale");
        saleDto.setStartDate(new Date(System.currentTimeMillis() - 86400000)); // 1 day in the past

        Sale sale = new Sale();
        sale.setName("Winter Sale");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(sale);

        assertThrows(InvalidRequestBodyException.class, () -> saleService.updateSaleSettings(saleDto),
                "The date is already gone,");
    }

    @Test
    void updateSaleSettingsThrowsFieldCannotBeEmptyExceptionForEmptyName() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName(""); // Empty name

        assertThrows(FieldCannotBeEmptyException.class, () -> saleService.updateSaleSettings(saleDto),
                "You must have a name");
    }

    @Test
    void updateSaleCategoriesSuccessfully() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Summer Sale");
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");
        saleDto.setCategoryDTOS(List.of(categoryDTO));

        Sale sale = new Sale();
        sale.setName("Summer Sale");
        Category category = new Category();
        category.setName("Electronics");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(sale);
        when(categoryRepository.findByName(categoryDTO.getName())).thenReturn(category);

        try {
            saleService.updateSaleCategories(saleDto);
        } catch (InvalidRequestBodyException | CategoryNotFoundException | SaleNotFoundException e) {
            e.printStackTrace();
        }

        verify(saleRepository, times(1)).save(saleArgumentCaptor.capture());

        Sale savedSale = saleArgumentCaptor.getValue();
        assertTrue(savedSale.getCategories().contains(category));
        assertEquals(savedSale.getCategories().get(0).getName(), saleDto.getCategoryDTOS().get(0).getName());
    }

    @Test
    void updateSaleCategoriesThrowsInvalidRequestBodyExceptionForNullSaleName() {
        SaleDto saleDto = new SaleDto();

        assertThrows(InvalidRequestBodyException.class, () -> saleService.updateSaleCategories(saleDto),
                "There is no sale set!");
    }

    @Test
    void updateSaleCategoriesThrowsCategoryNotFoundExceptionForNullCategoryList() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Winter Sale");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(new Sale());

        assertThrows(CategoryNotFoundException.class, () -> saleService.updateSaleCategories(saleDto),
                "Categories not set");
    }

    @Test
    void updateSaleCategoriesThrowsSaleNotFoundExceptionForNonExistentSale() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("NonExistentSale");
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Home Appliances");
        saleDto.setCategoryDTOS(List.of(categoryDTO));

        when(saleRepository.findByName(saleDto.getName())).thenReturn(null);

        assertThrows(SaleNotFoundException.class, () -> saleService.updateSaleCategories(saleDto),
                "Sale with name " + saleDto.getName() + " is not found!");
    }

    @Test
    void updateSaleCategoriesThrowsInvalidRequestBodyExceptionForNullCategoryName() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Spring Sale");
        CategoryDTO categoryDTO = new CategoryDTO();
        saleDto.setCategoryDTOS(List.of(categoryDTO));

        Sale sale = new Sale();
        sale.setName("Spring Sale");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(sale);

        assertThrows(InvalidRequestBodyException.class, () -> saleService.updateSaleCategories(saleDto),
                "There is no category name!");
    }

    @Test
    void updateSaleCategoriesThrowsCategoryNotFoundExceptionForNonExistentCategory() {
        SaleDto saleDto = new SaleDto();
        saleDto.setName("Autumn Sale");
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("NonExistentCategory");
        saleDto.setCategoryDTOS(List.of(categoryDTO));

        Sale sale = new Sale();
        sale.setName("Autumn Sale");

        when(saleRepository.findByName(saleDto.getName())).thenReturn(sale);
        when(categoryRepository.findByName(categoryDTO.getName())).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> saleService.updateSaleCategories(saleDto),
                "Category with name" + categoryDTO.getName() + " is not found");
    }

    @Test
    void deleteSaleSuccessfully() {
        String saleName = "Summer Sale";
        Sale sale = new Sale();
        sale.setName(saleName);

        when(saleRepository.findByName(saleName)).thenReturn(sale);

        try {
            saleService.deleteSale(saleName);
        } catch (SaleNotFoundException e) {
            e.printStackTrace();
        }

        verify(saleRepository, times(1)).delete(sale);
    }

    @Test
    void deleteSaleThrowsSaleNotFoundExceptionForNonExistentSale() {
        String saleName = "NonExistentSale";

        when(saleRepository.findByName(saleName)).thenReturn(null);

        assertThrows(SaleNotFoundException.class, () -> saleService.deleteSale(saleName),
                "Sale for delete is not found.");
    }
}
