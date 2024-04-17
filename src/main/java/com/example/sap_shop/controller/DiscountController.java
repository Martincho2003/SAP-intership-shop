package com.example.sap_shop.controller;

import com.example.sap_shop.dto.DiscountDTO;
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
        discountService.createDiscount(discountDTO);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update-settings")
    public ResponseEntity<?> updateDiscountSettings(@RequestBody DiscountDTO discountDTO){
        discountService.updateDiscountSettings(discountDTO);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update-products")
    public ResponseEntity<?> updateDiscountProducts(@RequestBody DiscountDTO discountDTO){
        discountService.updateDiscountProducts(discountDTO);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("/{discountName}")
    public ResponseEntity<?> deleteDiscount(@PathVariable String discountName){
        discountService.deleteDiscount(discountName);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/{discountName}")
    public ResponseEntity<?> searchDiscounts(@PathVariable String discountName){
        return ResponseEntity.ok(discountService.getDiscountsByName(discountName));
    }

}
