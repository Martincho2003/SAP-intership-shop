package com.example.sap_shop.controller;

import com.example.sap_shop.dto.DiscountDTO;
import com.example.sap_shop.error.DiscountNotFoundException;
import com.example.sap_shop.error.InvalidRequestBodyException;
import com.example.sap_shop.error.ProductNotFoundException;
import com.example.sap_shop.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/discount")
public class DiscountController {

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    public ResponseEntity<?> createDiscount(@RequestBody DiscountDTO discountDTO){
        try {
            discountService.createDiscount(discountDTO);
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update-settings")
    public ResponseEntity<?> updateDiscountSettings(@RequestBody DiscountDTO discountDTO){
        try {
            discountService.updateDiscountSettings(discountDTO);
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (DiscountNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update-products")
    public ResponseEntity<?> updateDiscountProducts(@RequestBody DiscountDTO discountDTO){
        try {
            discountService.updateDiscountProducts(discountDTO);
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (DiscountNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/delete/{discountName}")
    public ResponseEntity<?> deleteDiscount(@PathVariable String discountName){
        try {
            discountService.deleteDiscount(discountName);
        } catch (DiscountNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/search/{discountName}")
    public ResponseEntity<?> searchDiscounts(@PathVariable String discountName){
        return ResponseEntity.ok(discountService.getDiscountsByName(discountName));
    }

}
