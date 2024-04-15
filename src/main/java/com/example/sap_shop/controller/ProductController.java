package com.example.sap_shop.controller;

import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.CategoryNotFoundException;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.ProductAlreadyExistException;
import com.example.sap_shop.error.ProductNotFoundException;
import com.example.sap_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping
    public ResponseEntity<?> addNewProduct (@RequestBody ProductDTO productDTO) {
        try {
            productService.createProduct(productDTO);
            return ResponseEntity.ok().body("Product created successfully.");
        } catch (FieldCannotBeEmptyException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (ProductAlreadyExistException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (CategoryNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{productName}/assignCategory")
    public ResponseEntity<?> assignProductToCategory(@PathVariable String productName, @RequestParam String categoryName) {
        try {
            productService.updateCategory(productName, categoryName);
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
        return ResponseEntity.ok().body("Product assigned properly.");
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
            List<ProductDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProduct(@RequestParam String name) {

            List<ProductDTO> products = productService.findByNameContainingIgnoreCase(name);
            return ResponseEntity.ok(products);

    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteProduct(@PathVariable String name) {
        try {
            productService.deleteProduct(name);
            return ResponseEntity.ok().body("Product deleted successfully.");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }

    }

}
