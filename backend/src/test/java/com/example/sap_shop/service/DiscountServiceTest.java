package com.example.sap_shop.service;

import com.example.sap_shop.dto.DiscountDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.DiscountNotFoundException;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.ProductNotFoundException;
import com.example.sap_shop.model.Discount;
import com.example.sap_shop.model.Product;
import com.example.sap_shop.repository.DiscountRepository;
import com.example.sap_shop.repository.ProductRepository;
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
public class DiscountServiceTest {

    @MockBean
    private DiscountRepository discountRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private DiscountService discountService;

    @Captor
    private ArgumentCaptor<Discount> discountArgumentCaptor;

    private Product createMockedProduct(){
        Product product = new Product();
        product.setName("Product1");
        product.setQuantity(10);
        product.setPrice(100.0f);
        product.setDescription("Description1");
        return product;
    }

    @Test
    void createDiscountSuccessfully() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Summer Sale");
        discountDTO.setPercentage(20);
        discountDTO.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day from now
        discountDTO.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now
        ProductDTO productDTO = new ProductDTO("Product1", 10, 100.0f, "Description1");
        discountDTO.setProductDTOS(List.of(productDTO));

        Product product = createMockedProduct();
        when(productRepository.findByName(productDTO.getName())).thenReturn(product);

        try {
            discountService.createDiscount(discountDTO);
        } catch (InvalidRequestBodyException | ProductNotFoundException e) {
            e.printStackTrace();
        }

        verify(discountRepository, times(1)).save(discountArgumentCaptor.capture());

        assertEquals(discountArgumentCaptor.getValue().getName(), discountDTO.getName());
        assertEquals(discountArgumentCaptor.getValue().getProducts().get(0).getName(), product.getName());
    }

    @Test
    void createDiscountThrowsInvalidRequestBodyExceptionForNullFields() {
        DiscountDTO discountDTO = new DiscountDTO();
        // All fields are null

        assertThrows(InvalidRequestBodyException.class, () -> discountService.createDiscount(discountDTO),
                "Some of the settings are not set");
    }

    @Test
    void createDiscountThrowsInvalidRequestBodyExceptionForInvalidSettings() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("");
        discountDTO.setPercentage(-10);
        discountDTO.setStartDate(new Date(System.currentTimeMillis() - 86400000)); // 1 day ago
        discountDTO.setEndDate(new Date(System.currentTimeMillis() - 86400000 * 2)); // 2 days ago

        assertThrows(InvalidRequestBodyException.class, () -> discountService.createDiscount(discountDTO),
                "Some of the settings are set wrong");
    }

    @Test
    void createDiscountThrowsProductNotFoundExceptionForNonExistentProduct() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Summer Sale");
        discountDTO.setPercentage(20);
        discountDTO.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day from now
        discountDTO.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now
        ProductDTO productDTO = new ProductDTO("NonExistentProduct", 10, 100.0f, "Description1");
        discountDTO.setProductDTOS(List.of(productDTO));

        when(productRepository.findByName(productDTO.getName())).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> discountService.createDiscount(discountDTO),
                "Product with name " + productDTO.getName() + " not found!");
    }

    @Test
    void createDiscountThrowsInvalidRequestBodyExceptionForPastStartDate() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Winter Sale");
        discountDTO.setPercentage(15);
        discountDTO.setStartDate(new Date(System.currentTimeMillis() - 86400000)); // 1 day in the past
        discountDTO.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now
        ProductDTO productDTO = new ProductDTO("Product1", 10, 100.0f, "Description1");
        discountDTO.setProductDTOS(List.of(productDTO));

        assertThrows(InvalidRequestBodyException.class, () -> discountService.createDiscount(discountDTO),
                "Some of the settings are set wrong");
    }

    @Test
    void createDiscountThrowsInvalidRequestBodyExceptionForEndDateBeforeStartDate() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Spring Sale");
        discountDTO.setPercentage(25);
        discountDTO.setStartDate(new Date(System.currentTimeMillis() + 86400000 * 5)); // 5 days from now
        discountDTO.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 3)); // 3 days from now
        ProductDTO productDTO = new ProductDTO("Product2", 20, 200.0f, "Description2");
        discountDTO.setProductDTOS(List.of(productDTO));

        assertThrows(InvalidRequestBodyException.class, () -> discountService.createDiscount(discountDTO),
                "Some of the settings are set wrong");
    }

    @Test
    void updateDiscountSettingsSuccessfully() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Spring Sale");
        discountDTO.setPercentage(15);
        discountDTO.setStartDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day from now
        discountDTO.setEndDate(new Date(System.currentTimeMillis() + 86400000 * 10)); // 10 days from now

        Discount discount = new Discount();
        discount.setName("Spring Sale");

        when(discountRepository.findByName(discountDTO.getName())).thenReturn(discount);

        try {
            discountService.updateDiscountSettings(discountDTO);
        } catch (InvalidRequestBodyException | DiscountNotFoundException e) {
            e.printStackTrace();
        }

        verify(discountRepository, times(1)).save(discountArgumentCaptor.capture());
        Discount discount1 = discountArgumentCaptor.getValue();
        assertEquals(discountDTO.getName(), discount1.getName());
        assertEquals(discountDTO.getPercentage(), discount1.getPercentage());
        assertEquals(discountDTO.getStartDate(), discount1.getStartDate());
        assertEquals(discountDTO.getEndDate(), discount1.getEndDate());
    }

    @Test
    void updateDiscountSettingsThrowsInvalidRequestBodyExceptionForNullName() {
        DiscountDTO discountDTO = new DiscountDTO();

        assertThrows(InvalidRequestBodyException.class, () -> discountService.updateDiscountSettings(discountDTO),
                "There is no discount name");
    }

    @Test
    void updateDiscountSettingsThrowsDiscountNotFoundExceptionForNonExistentDiscount() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("NonExistentDiscount");

        when(discountRepository.findByName(discountDTO.getName())).thenReturn(null);

        assertThrows(DiscountNotFoundException.class, () -> discountService.updateDiscountSettings(discountDTO),
                "Discount with name: " + discountDTO.getName() + " is not found.");
    }

    @Test
    void updateDiscountSettingsThrowsInvalidRequestBodyExceptionForNegativePercentage() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Spring Sale");
        discountDTO.setPercentage(-10);

        Discount discount = new Discount();
        discount.setName("Spring Sale");

        when(discountRepository.findByName(discountDTO.getName())).thenReturn(discount);

        assertThrows(InvalidRequestBodyException.class, () -> discountService.updateDiscountSettings(discountDTO),
                "Percentage can't be under 0.");
    }

    @Test
    void updateDiscountProductsSuccessfully() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Summer Sale");
        ProductDTO productDTO1 = new ProductDTO("Product1", 10, 100.0f, "Description1");
        ProductDTO productDTO2 = new ProductDTO("Product2", 20, 200.0f, "Description2");
        discountDTO.setProductDTOS(List.of(productDTO1, productDTO2));

        Discount discount = new Discount();
        discount.setName("Summer Sale");
        Product product1 = createMockedProduct();
        Product product2 = new Product();
        product2.setName("Product2");
        product2.setQuantity(20);
        product2.setPrice(200.0f);
        product2.setDescription("Description2");

        when(discountRepository.findByName(discountDTO.getName())).thenReturn(discount);
        when(productRepository.findByName(productDTO1.getName())).thenReturn(product1);
        when(productRepository.findByName(productDTO2.getName())).thenReturn(product2);

        try {
            discountService.updateDiscountProducts(discountDTO);
        } catch (InvalidRequestBodyException | DiscountNotFoundException e) {
            e.printStackTrace();
        }

        verify(discountRepository, times(1)).save(discountArgumentCaptor.capture());

        Discount discountSaved = discountArgumentCaptor.getValue();
        assertTrue(discountSaved.getProducts().contains(product1));
        assertEquals(discountSaved.getProducts().get(0).getName(), product1.getName());
        assertTrue(discountSaved.getProducts().contains(product2));
        assertEquals(discountSaved.getProducts().get(1).getName(), product2.getName());
    }

    @Test
    void updateDiscountProductsThrowsInvalidRequestBodyExceptionForNullProductList() {
        DiscountDTO discountDTO = new DiscountDTO();
        discountDTO.setName("Winter Sale");

        when(discountRepository.findByName(discountDTO.getName())).thenReturn(new Discount());

        assertThrows(InvalidRequestBodyException.class, () -> discountService.updateDiscountProducts(discountDTO),
                "There is no products");
    }

    @Test
    void deleteDiscountSuccessfully() {
        String discountName = "Summer Sale";
        Discount discount = new Discount();
        discount.setName(discountName);

        when(discountRepository.findByName(discountName)).thenReturn(discount);

        try {
            discountService.deleteDiscount(discountName);
        } catch (DiscountNotFoundException | InvalidRequestBodyException e) {
            e.printStackTrace();
        }

        verify(discountRepository, times(1)).delete(discount);
    }
}
