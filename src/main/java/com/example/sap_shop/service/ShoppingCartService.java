package com.example.sap_shop.service;

import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.model.JwtUtil;
import com.example.sap_shop.model.OrderItem;
import com.example.sap_shop.model.ShoppingCart;
import com.example.sap_shop.repository.OrderItemRepository;
import com.example.sap_shop.repository.ProductRepository;
import com.example.sap_shop.repository.ShoppingCartRepository;
import com.example.sap_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserRepository userRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, JwtUtil jwtUtil) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.jwtUtil = jwtUtil;
    }

    public ShoppingCartDTO getShoppingCart(String token){
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token)));
        ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        for(OrderItem orderItem : shoppingCart.getOrderItems()){
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(orderItem.getProduct().getName());
            productDTO.setQuantity(orderItem.getProduct().getQuantity());
            productDTO.setPrice(orderItem.getProduct().getPrice());
            productDTO.setDescription(orderItem.getProduct().getDescription());
            orderItemDTO.setQuantity(orderItem.getQuantity());
            orderItemDTO.setProduct(productDTO);
            orderItemDTOS.add(orderItemDTO);
        }
        shoppingCartDTO.setOrderItemDTOS(orderItemDTOS);
        return shoppingCartDTO;
    }

    public void addProductToShoppingCart(String token, OrderItemDTO orderItemDTO){
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token)));
        List<OrderItem> orderItems = shoppingCart.getOrderItems();
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(productRepository.findByName(orderItemDTO.getProduct().getName()));
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItemRepository.save(orderItem);
        orderItems.add(orderItem);
        shoppingCart.setOrderItems(orderItems);
        shoppingCartRepository.save(shoppingCart);
    }

    public void removeProductFromShoppingCart(String token, OrderItemDTO orderItemDTO){
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token)));
        List<OrderItem> orderItems = shoppingCart.getOrderItems();
        OrderItem orderItemToDelete = orderItemRepository.findByProduct(productRepository.findByName(orderItemDTO.getProduct().getName()));
        orderItems.remove(orderItemToDelete);
        shoppingCart.setOrderItems(orderItems);
        orderItemRepository.delete(orderItemToDelete);
        shoppingCartRepository.save(shoppingCart);
    }

    public void changeProductQuantityToShoppingCart(String token, OrderItemDTO orderItemDTO){
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(userRepository.findByUsername(jwtUtil.extractUsername(token)));
        List<OrderItem> orderItems = shoppingCart.getOrderItems();
        OrderItem orderItemToChange = orderItemRepository.findByProduct(productRepository.findByName(orderItemDTO.getProduct().getName()));
        orderItemToChange.setQuantity(orderItemDTO.getQuantity());
        orderItemRepository.save(orderItemToChange);
        orderItems.get(orderItems.indexOf(orderItemToChange)).setQuantity(orderItemDTO.getQuantity());
        shoppingCart.setOrderItems(orderItems);
        shoppingCartRepository.save(shoppingCart);
    }
}
