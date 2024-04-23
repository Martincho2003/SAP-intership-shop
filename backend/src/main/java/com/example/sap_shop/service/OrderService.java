package com.example.sap_shop.service;

import com.example.sap_shop.dto.OrderDTO;
import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.error.InvalidBuyException;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.ShoppingCartDoesNotExistError;
import com.example.sap_shop.error.TokenExpiredException;
import com.example.sap_shop.model.*;
import com.example.sap_shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public OrderDTO buy(String token) throws ShoppingCartDoesNotExistError, TokenExpiredException, InvalidBuyException {
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
        order.setTotalPrice(totalPrice);

        List<OrderItem> orderList = new ArrayList<>();
        if (shoppingCartDTO.getOrderItemDTOS().size() == 0) {
            throw new InvalidBuyException("Your shopping cart is empty");
        }
        for (OrderItemDTO orderItemDTO : shoppingCartDTO.getOrderItemDTOS()) {
            Optional<OrderItem> orderItem = orderItemRepository.findById(orderItemDTO.getId());
            orderList.add(orderItem.get());
            Product product = productRepository.findByName(orderItemDTO.getProduct().getName());
            product.setQuantity(product.getQuantity() - orderItemDTO.getQuantity());
            productRepository.save(product);
        }
        order.setOrderItems(orderList);

        order = orderRepository.save(order);
        for(OrderItem orderItem : orderList){
            try {
                shoppingCartService.removeProductFromShoppingCart(token, orderItem.getId().toString());
            } catch (InvalidRequestBodyException e) {
                throw new RuntimeException(e);
            }
        }
        orders.add(order);
        user.setOrders(orders);
        userRepository.save(user);
        return orderDTO;
    }
}
