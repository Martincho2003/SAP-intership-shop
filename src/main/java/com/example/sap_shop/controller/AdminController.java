package com.example.sap_shop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin/resource")
    public String adminResource() {
        return "This is an admin resource.";
    }
}