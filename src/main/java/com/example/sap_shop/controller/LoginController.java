package com.example.sap_shop.controller;

import com.example.sap_shop.CustomUserDetailsService;
import com.example.sap_shop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class LoginController {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/loginpage")
    public String login() {
        return "login";
    }

    @PostMapping("/loginpage")
    public ResponseEntity loginPost(@RequestBody User user) {
        HashMap<String, String> userAnswer = new HashMap<>();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        userAnswer.put("username", userDetails.getUsername());
        userAnswer.put("expired", String.valueOf(userDetails.isAccountNonExpired()));

        if(userDetails.isAccountNonExpired()){
            return new ResponseEntity(userAnswer, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity(new HashMap<>(), HttpStatusCode.valueOf(404));
    }
}