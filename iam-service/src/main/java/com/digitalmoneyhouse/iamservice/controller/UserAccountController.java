package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.UserAccountResponse;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAccountController {
    @Autowired
    private UserAccountService service;

    @PostMapping
    public ResponseEntity<UserAccountResponse> save(@RequestBody UserAccount userAccount) {
        return ResponseEntity.status(201).body(service.save(userAccount));
    }

}
