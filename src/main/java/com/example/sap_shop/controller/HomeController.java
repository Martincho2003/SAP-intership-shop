package com.example.sap_shop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public @ResponseBody Map<String, String > getHomePageInfo(){
        HashMap<String, String> map = new HashMap<>();
        map.put("name", "meso");
        return map;
    }

}
