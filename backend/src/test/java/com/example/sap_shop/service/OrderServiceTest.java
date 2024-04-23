package com.example.sap_shop.service;

import com.example.sap_shop.dto.OrderDTO;
import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.error.InvalidBuyException;
import com.example.sap_shop.error.ShoppingCartDoesNotExistError;
import com.example.sap_shop.error.TokenExpiredException;
import com.example.sap_shop.model.*;
import com.example.sap_shop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private ShoppingCartService shoppingCartService;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private OrderItemRepository orderItemRepository;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderArgumentCaptor;

    private final String token = "Bearer valid.token";

    @BeforeEach
    void setup() {
        // Setup common mocking details
        User user = new User();
        user.setUsername("user1");

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(user);
    }

    @Test
    void buy_ThrowsShoppingCartDoesNotExistError_WhenCartIsEmpty() throws TokenExpiredException {
        when(shoppingCartService.getShoppingCart(token)).thenReturn(null);

        assertThrows(ShoppingCartDoesNotExistError.class, () -> orderService.buy(token));
    }

    @Test
    void buy_SuccessfullyCreatesOrder() throws Exception {
        // Setup token and user
        String token = "dummyToken";
        User user = new User();
        user.setUsername("user1");
        List<Order> orders = new ArrayList<>();
        user.setOrders(orders);

        // Setup Product and OrderItem
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99f);
        product.setQuantity(10);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L); // Ensure the ID is set
        orderItem.setProduct(product);
        orderItem.setQuantity(1);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // Setup ShoppingCart and ShoppingCartDTO
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setOrderItems(orderItems);

        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        OrderItemDTO orderItemDTO = new OrderItemDTO(orderItem.getId(), new ProductDTO("Laptop", "Description", 999.99f, 999.99f, 10, "path", 950.00f, "Electronics"), 1);
        shoppingCartDTO.setOrderItemDTOS(List.of(orderItemDTO));

        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(jwtUtil.extractUsername(anyString())).thenReturn("user1");
        when(shoppingCartService.getShoppingCart(token)).thenReturn(shoppingCartDTO);
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(orderItem));
        when(productRepository.findByName("Laptop")).thenReturn(product);

        // Execute the buy method
        OrderDTO result = orderService.buy(token);

        // Assertions and verifications
        assertNotNull(result);
        assertEquals("In progress", result.getStatus());
        assertEquals(1, result.getOrderItems().size());
        assertEquals(999.99f, result.getTotalPrice());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void buy_ThrowsInvalidBuyException_WhenCartIsNotEmptyButOrderItemsMissing() throws TokenExpiredException {
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        shoppingCartDTO.setOrderItemDTOS(Arrays.asList()); // Empty order items

        when(shoppingCartService.getShoppingCart(token)).thenReturn(shoppingCartDTO);

        assertThrows(InvalidBuyException.class, () -> orderService.buy(token));
    }
}