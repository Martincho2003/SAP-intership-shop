package com.example.sap_shop.controller;

import com.example.sap_shop.CustomUserDetailsService;
import com.example.sap_shop.model.JwtUtil;
import com.example.sap_shop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class LoginController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/loginpage")
    public String login() {
        return "login";
    }

    @PostMapping("/loginpage")
    public ResponseEntity loginPost(@RequestBody User user) {
        HashMap<String, String> userAnswer = new HashMap<>();
        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            if(passwordEncoder.matches(user.getPassword(), userDetails.getPassword())){
                String jwtToken = jwtUtil.generateToken(userDetails.getUsername());
                userAnswer.put("username", userDetails.getUsername());
                userAnswer.put("token", jwtToken);
                return ResponseEntity.ok(userAnswer);
            } else{
                userAnswer.put("error", "Password is incorrect");
                return ResponseEntity.status(403).body(userAnswer);
            }
        } catch (UsernameNotFoundException e){
            userAnswer.put("error", e.getMessage());
            return ResponseEntity.status(404).body(userAnswer);
        }
    }
}