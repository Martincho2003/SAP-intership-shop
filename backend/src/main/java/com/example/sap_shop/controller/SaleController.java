package com.example.sap_shop.controller;

import com.example.sap_shop.dto.SaleDto;
import com.example.sap_shop.error.*;
import com.example.sap_shop.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> addSale(@RequestBody SaleDto saleDto){
        try {
            saleService.createSale(saleDto);
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update-settings")
    public ResponseEntity<?> updateSaleSettings(@RequestBody SaleDto saleDto){
        try {
            saleService.updateSaleSettings(saleDto);
        } catch (InvalidRequestBodyException | FieldCannotBeEmptyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update-categories")
    public ResponseEntity<?> updateSaleCategories(@RequestBody SaleDto saleDto){
        try {
            saleService.updateSaleCategories(saleDto);
        } catch (InvalidRequestBodyException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (CategoryNotFoundException | SaleNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSaleByName(@RequestParam String name){
        return ResponseEntity.ok(saleService.getSalesByName(name));
    }

    @DeleteMapping("/delete/{saleName}")
    public ResponseEntity<?> deleteSale(@PathVariable String saleName){
        try {
            saleService.deleteSale(saleName);
        } catch (SaleNotFoundException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }
}
