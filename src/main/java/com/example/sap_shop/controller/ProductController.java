package com.example.sap_shop.controller;

import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.dto.UserDto;
import com.example.sap_shop.error.EmptyCredentialException;
import com.example.sap_shop.error.ProductAlreadyExistException;
import com.example.sap_shop.error.UserAlreadyExistException;
import com.example.sap_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController()
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping(path = "/product/create")
    public ResponseEntity addNewProduct (@RequestBody ProductDTO productDTO) {
        HashMap<String, String> answer = new HashMap<>();
        try {
            productService.createProduct(productDTO);
            answer.put("message", "success");
            return ResponseEntity.ok(answer);
        } catch (ProductAlreadyExistException e) {
            answer.put("error", e.getMessage());
            return ResponseEntity.status(409).body(answer);
        }
    }
}
