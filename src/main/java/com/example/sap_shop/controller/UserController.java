package com.example.sap_shop.controller;

import com.example.sap_shop.model.User;
import com.example.sap_shop.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/test")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        User n = new User();
        n.setName(name);
        n.setEmail(email);
        n.setPassword(String.valueOf(password.hashCode()));
        userRepository.save(n);
        return "Saved";
    }

    /*@GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }*/
}
