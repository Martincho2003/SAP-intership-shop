package com.example.sap_shop.controller;

import com.example.sap_shop.dto.UserDto;
import com.example.sap_shop.error.EmptyCredentialException;
import com.example.sap_shop.error.InvalidLoginCredentialException;
import com.example.sap_shop.error.UserAlreadyExistException;
import com.example.sap_shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path="/signup")
    public ResponseEntity addNewUser (@RequestBody UserDto userDto) {
        HashMap<String, String> answer = new HashMap<>();
        try {
            userService.registerNewUser(userDto);
            return ResponseEntity.ok(answer);
        } catch (UserAlreadyExistException uaeEx) {
            answer.put("error", uaeEx.getMessage());
            return ResponseEntity.status(409).body(answer);
        } catch (EmptyCredentialException e) {
            answer.put("error", e.getMessage());
            return ResponseEntity.status(403).body(answer);
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginPost(@RequestBody UserDto userDto) {
        HashMap<String, String> answer = new HashMap<>();
        try {
            String token = userService.loginUser(userDto);
            answer.put("token", token);
            return ResponseEntity.ok(answer);
        } catch (InvalidLoginCredentialException e) {
            answer.put("error", e.getMessage());
            return ResponseEntity.status(401).body(answer);
        }
    }

    @GetMapping("/user")
    public ResponseEntity getProfile(@RequestHeader("Authorization") String token){
        HashMap<String, String> answer = new HashMap<>();

        UserDto userDto = userService.getProfileInfo(token);
        answer.put("name", userDto.getUsername());
        return ResponseEntity.ok(answer);
    }
}
