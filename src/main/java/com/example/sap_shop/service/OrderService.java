package com.example.sap_shop.service;

import com.example.sap_shop.dto.OrderDTO;
import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.error.ShoppingCartDoesNotExistError;
import com.example.sap_shop.model.*;
import com.example.sap_shop.repository.*;
import jakarta.transaction.Transactional;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private JwtUtil jwtUtil;
    private UserRepository userRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ShoppingCartService shoppingCartService, ProductRepository productRepository, OrderItemRepository orderItemRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.shoppingCartService = shoppingCartService;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderDTO buy(String token) throws ShoppingCartDoesNotExistError {
        User user = userRepository.findByUsername(jwtUtil.extractUsername(token.substring(7)));
        List<Order> orders = user.getOrders();
        OrderDTO orderDTO = new OrderDTO();
        ShoppingCartDTO shoppingCartDTO = shoppingCartService.getShoppingCart(token);

        if (shoppingCartDTO == null) {
            throw new ShoppingCartDoesNotExistError("Shopping cart doesn't exist.");
        }

        orderDTO.setOrderDate(new Date());
        orderDTO.setStatus("In progress");
        orderDTO.setOrderItems(shoppingCartDTO.getOrderItemDTOS());
        Float totalPrice = 0.0f;

        for (OrderItemDTO orderItemDTO : shoppingCartDTO.getOrderItemDTOS()) {
            totalPrice += orderItemDTO.getProduct().getPrice() * orderItemDTO.getQuantity();
        }
        orderDTO.setTotalPrice(totalPrice);

        Order order = new Order();
        order.setOrderDate(orderDTO.getOrderDate());
        order.setStatus(orderDTO.getStatus());

        List<OrderItem> orderList = new ArrayList<>();
        for (OrderItemDTO orderItemDTO : shoppingCartDTO.getOrderItemDTOS()) {
            OrderItem orderItem = orderItemRepository.findByProductName(orderItemDTO.getProduct().getName());
            orderList.add(orderItem);
            Product product = productRepository.findByName(orderItemDTO.getProduct().getName());
            product.setQuantity(product.getQuantity() - orderItemDTO.getQuantity());
            productRepository.save(product);
        }
        order.setOrderItems(orderList);

        orders.add(order);
        user.setOrders(orders);
        userRepository.save(user);
        orderRepository.save(order);
        return orderDTO;
    }
}