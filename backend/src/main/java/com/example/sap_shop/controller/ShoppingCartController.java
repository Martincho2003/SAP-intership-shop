package com.example.sap_shop.controller;

import com.example.sap_shop.dto.OrderItemDTO;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.NotEnoughQuantityException;
import com.example.sap_shop.error.TokenExpiredException;
import com.example.sap_shop.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ResponseEntity<?> getShoppingCart(@RequestHeader("Authorization") String token){
        try {
            return ResponseEntity.ok(shoppingCartService.getShoppingCart(token));
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addProductToShoppingCart(@RequestHeader("Authorization") String token, @RequestBody OrderItemDTO orderItemDTO){
        try {
            shoppingCartService.addProductToShoppingCart(token, orderItemDTO);
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (NotEnoughQuantityException | InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping ResponseEntity<?> deleteProductFromShoppingCart(@RequestHeader("Authorization") String token, @RequestBody OrderItemDTO orderItemDTO){
        try {
            shoppingCartService.removeProductFromShoppingCart(token, orderItemDTO);
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update")
    public ResponseEntity<?> changeProductQuantityInShoppingCart(@RequestHeader("Authorization") String token, @RequestBody OrderItemDTO orderItemDTO){
        try {
            shoppingCartService.changeProductQuantityToShoppingCart(token, orderItemDTO);
        } catch (TokenExpiredException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (NotEnoughQuantityException | InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }
}
