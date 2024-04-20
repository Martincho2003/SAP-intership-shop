package com.example.sap_shop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.NotEnoughQuantityException;
import com.example.sap_shop.error.TokenExpiredException;
import com.example.sap_shop.model.*;
import com.example.sap_shop.repository.ShoppingCartRepository;
import com.example.sap_shop.repository.UserRepository;
import com.example.sap_shop.repository.OrderItemRepository;
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
import java.util.Date;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ShoppingCartServiceTest {

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderItemRepository orderItemRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Captor
    private ArgumentCaptor<ShoppingCart> shoppingCartArgumentCaptor;

    private Product createMockedProduct(){
        Product product = new Product();
        product.setName("Product1");
        product.setQuantity(10);
        product.setPrice(100.0f);
        product.setDescription("Description1");
        return product;
    }

    private ProductDTO createMockedProductDTO(){
        ProductDTO product = new ProductDTO();
        product.setName("Product1");
        product.setQuantity(10);
        product.setPrice(100.0f);
        product.setDescription("Description1");
        return product;
    }


    @Test
    void getShoppingCartReturnsCorrectData() throws TokenExpiredException {
        String token = "Bearer validToken";
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = createMockedProduct();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        shoppingCart.setOrderItems(Arrays.asList(orderItem));

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        ShoppingCartDTO shoppingCartDTO = shoppingCartService.getShoppingCart(token);

        assertNotNull(shoppingCartDTO);
        assertEquals(1, shoppingCartDTO.getOrderItemDTOS().size());
        OrderItemDTO resultOrderItemDTO = shoppingCartDTO.getOrderItemDTOS().get(0);
        assertEquals("Product1", resultOrderItemDTO.getProduct().getName());
        assertEquals(2, resultOrderItemDTO.getQuantity());
    }

    @Test
    void getShoppingCartThrowsTokenExpiredException() {
        String token = "Bearer expiredToken";
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() - 10000)); // Token expired 10 seconds ago

        assertThrows(TokenExpiredException.class, () -> shoppingCartService.getShoppingCart(token));
    }

    @Test
    void addProductToShoppingCartSuccessfully() throws Exception {
        String token = "Bearer validToken";
        ProductDTO productDTO = createMockedProductDTO();
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 1);
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = createMockedProduct();

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);
        when(productRepository.findByName(orderItemDTO.getProduct().getName())).thenReturn(product);
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        shoppingCartService.addProductToShoppingCart(token, orderItemDTO);

        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(shoppingCartRepository, times(1)).save(shoppingCartArgumentCaptor.capture());

        assertEquals(shoppingCartArgumentCaptor.getValue().getOrderItems().size(), 1);
        assertEquals(shoppingCartArgumentCaptor.getValue().getOrderItems().get(0).getProduct().getName(), product.getName());
    }

    @Test
    void addProductToShoppingCartThrowsNotEnoughQuantityException() {
        String token = "Bearer validToken";
        ProductDTO productDTO = new ProductDTO("Product1", 0, 100.0f, "Description1"); // Quantity is 0
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 1);
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = new Product();
        product.setName("Product1");
        product.setQuantity(0);
        product.setPrice(100.0f);
        product.setDescription("Description1");

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);
        when(productRepository.findByName(orderItemDTO.getProduct().getName())).thenReturn(product);
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        Exception exception = assertThrows(NotEnoughQuantityException.class, () -> shoppingCartService.addProductToShoppingCart(token, orderItemDTO));

        assertEquals(exception.getMessage(), "Sorry, but for this moment we do not have this quantity of the product.");
    }

    @Test
    void addProductToShoppingCartThrowsInvalidRequestBodyExceptionForNullProductName() {
        String token = "Bearer validToken";
        ProductDTO productDTO = new ProductDTO(null, 10, 100.0f, "Description1"); // Product name is empty
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 0); // Quantity is 0

        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        assertThrows(InvalidRequestBodyException.class, () -> shoppingCartService.addProductToShoppingCart(token, orderItemDTO),
                "There is not product name to add!");
    }

    @Test
    void addProductToShoppingCartThrowsInvalidRequestBodyExceptionForEmptyProductName() {
        String token = "Bearer validToken";
        ProductDTO productDTO = new ProductDTO("", 10, 100.0f, "Description1"); // Product name is empty
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 1);

        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        assertThrows(InvalidRequestBodyException.class, () -> shoppingCartService.addProductToShoppingCart(token, orderItemDTO),
                "There is not product name to add!");
    }

    @Test
    void removeProductFromShoppingCartSuccessfully() throws Exception {
        String token = "Bearer validToken";
        ProductDTO productDTO = new ProductDTO("Product1", 10, 100.0f, "Description1");
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 1);
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = createMockedProduct();
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setProduct(product);
        shoppingCart.setOrderItems(Arrays.asList(orderItem));

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);
        when(productRepository.findByName(orderItemDTO.getProduct().getName())).thenReturn(product);
        when(orderItemRepository.findByProduct(product)).thenReturn(orderItem);
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        shoppingCartService.removeProductFromShoppingCart(token, orderItemDTO);

        verify(orderItemRepository, times(1)).delete(orderItem);
        verify(shoppingCartRepository, times(1)).save(shoppingCartArgumentCaptor.capture());

        assertEquals(shoppingCartArgumentCaptor.getValue().getOrderItems().size(), 0);
    }

    @Test
    void removeProductFromShoppingCartThrowsInvalidRequestBodyExceptionForNullProduct() {
        String token = "Bearer validToken";
        OrderItemDTO orderItemDTO = new OrderItemDTO(null, 1);

        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        assertThrows(InvalidRequestBodyException.class, () -> shoppingCartService.removeProductFromShoppingCart(token, orderItemDTO),
                "You don't have product to add!");
    }

    @Test
    void removeProductFromShoppingCartThrowsInvalidRequestBodyExceptionForEmptyProductName() {
        String token = "Bearer validToken";
        ProductDTO productDTO = new ProductDTO("", 10, 100.0f, "Description1"); // Product name is empty
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 1);

        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        assertThrows(InvalidRequestBodyException.class, () -> shoppingCartService.removeProductFromShoppingCart(token, orderItemDTO),
                "There is not product name to add!");
    }

    @Test
    void changeProductQuantityToShoppingCartSuccessfully() throws Exception {
        String token = "Bearer validToken";
        ProductDTO productDTO = createMockedProductDTO();
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 5);
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = createMockedProduct();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        shoppingCart.setOrderItems(Arrays.asList(orderItem));

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);
        when(productRepository.findByName(orderItemDTO.getProduct().getName())).thenReturn(product);
        when(orderItemRepository.findByProduct(product)).thenReturn(orderItem);
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        shoppingCartService.changeProductQuantityToShoppingCart(token, orderItemDTO);

        verify(orderItemRepository, times(1)).save(orderItem);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        assertEquals(orderItemDTO.getQuantity(), orderItem.getQuantity());
    }

    @Test
    void changeProductQuantityToShoppingCartThrowsNotEnoughQuantityException() {
        String token = "Bearer validToken";
        ProductDTO productDTO = createMockedProductDTO();
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, 15);
        User user = new User();
        ShoppingCart shoppingCart = new ShoppingCart();
        Product product = createMockedProduct();

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);
        when(productRepository.findByName(orderItemDTO.getProduct().getName())).thenReturn(product);
        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        assertThrows(NotEnoughQuantityException.class, () -> shoppingCartService.changeProductQuantityToShoppingCart(token, orderItemDTO),
                "Sorry, but for this moment we do not have this quantity of the product.");
    }

    @Test
    void changeProductQuantityToShoppingCartThrowsInvalidRequestBodyExceptionForInvalidQuantity() {
        String token = "Bearer validToken";
        ProductDTO productDTO = createMockedProductDTO();
        OrderItemDTO orderItemDTO = new OrderItemDTO(productDTO, -1); // Invalid quantity

        when(jwtUtil.extractExpiration(token.substring(7))).thenReturn(new Date(System.currentTimeMillis() + 10000)); // Token expires in 10 seconds

        assertThrows(InvalidRequestBodyException.class, () -> shoppingCartService.changeProductQuantityToShoppingCart(token, orderItemDTO),
                "You can't have quantity under 0!");
    }
}