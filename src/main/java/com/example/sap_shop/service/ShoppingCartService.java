package com.example.sap_shop.service;

import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.ShoppingCartDTO;
import com.example.sap_shop.model.JwtUtil;
import com.example.sap_shop.model.OrderItem;
import com.example.sap_shop.model.ShoppingCart;
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
    private final JwtUtil jwtUtil;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
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
}
