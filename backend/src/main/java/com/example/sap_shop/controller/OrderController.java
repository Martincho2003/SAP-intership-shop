package com.example.sap_shop.controller;

import com.example.sap_shop.error.ShoppingCartDoesNotExistError;
import com.example.sap_shop.error.TokenExpiredException;
import com.example.sap_shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> buyOrder(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.ok(orderService.buy(token));
        } catch (ShoppingCartDoesNotExistError e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
