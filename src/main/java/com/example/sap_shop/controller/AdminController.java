package com.example.sap_shop.controller;

import com.example.sap_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/resource")
    public String adminResource() {
        return "This is an admin resource.";
    }

    @PostMapping("/update_role/{username}")
    public String updateUserRole(@PathVariable String username, @RequestParam List<String> roles){
        userService.updateUserRole(username, roles);
        return "success";
    }
}