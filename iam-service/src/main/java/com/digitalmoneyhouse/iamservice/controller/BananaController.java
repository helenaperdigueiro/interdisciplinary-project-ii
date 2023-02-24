package com.digitalmoneyhouse.iamservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BananaController {

    @GetMapping("/banana")
    public String getBanana(){

        return "Banana";
    }
}
