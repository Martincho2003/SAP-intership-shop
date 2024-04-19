package com.example.sap_shop.service;

import com.example.sap_shop.repository.DiscountRepository;
import com.example.sap_shop.repository.ProductRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DiscountServiceTest {

    @MockBean
    private DiscountRepository discountRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private DiscountService discountService;
}
