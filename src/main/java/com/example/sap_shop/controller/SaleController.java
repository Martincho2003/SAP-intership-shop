package com.example.sap_shop.controller;

import com.example.sap_shop.dto.SaleDto;
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

    @PostMapping
    public ResponseEntity<?> addSale(@RequestBody SaleDto saleDto){
        saleService.createSale(saleDto);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateSale(@RequestBody SaleDto saleDto){
        saleService.updateSale(saleDto);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSaleByName(@RequestParam String name){
        return ResponseEntity.ok(saleService.getSaleByName(name));
    }

    @DeleteMapping("/delete/{saleName}")
    public ResponseEntity<?> deleteSale(@PathVariable String saleName){
        saleService.deleteSale(saleName);
        return ResponseEntity.ok("Success");
    }
}
