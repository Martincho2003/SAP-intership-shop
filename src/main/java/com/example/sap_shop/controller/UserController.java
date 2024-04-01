package com.example.sap_shop.controller;

import com.example.sap_shop.model.User;
import com.example.sap_shop.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/signup")
    public @ResponseBody String addNewUser (@RequestBody User user) {
        System.out.println(user.getRoles());
        userRepository.save(user);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
