package com.digitalmoneyhouse.iamservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthCheck {
    @GetMapping
    public ResponseEntity<Map<String, String>> check() {
        Map<String, String> map = new HashMap<>();
        map.put("status", "Accounts Service is UP!");
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }
}
