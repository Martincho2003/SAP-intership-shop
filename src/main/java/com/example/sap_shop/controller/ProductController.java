package com.example.sap_shop.controller;

import com.example.sap_shop.dto.ProductDTO;
import com.example.sap_shop.error.FieldCannotBeEmptyException;
import com.example.sap_shop.error.ProductAlreadyExistException;
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
        } catch (ProductAlreadyExistException e) {
            return ResponseEntity.status(409).body("Error: " + e.getMessage());
        } catch (FieldCannotBeEmptyException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductDTO> products = productService.findAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve products: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> findByNameContainingIgnoreCase(@RequestParam String name) {
        try {
            List<ProductDTO> products = productService.findByNameContainingIgnoreCase(name);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to search products by name: " + e.getMessage());
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteProduct(@PathVariable String name) {
        try {
            productService.deleteProduct(name);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (RuntimeException e) { // Assuming RuntimeException covers not found scenarios; adjust as necessary
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete product: " + e.getMessage());
        }
    }

}
