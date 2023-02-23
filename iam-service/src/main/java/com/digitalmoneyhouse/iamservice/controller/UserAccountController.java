package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.ConfirmRegistration;
import com.digitalmoneyhouse.iamservice.dto.GenericSucessResponse;
import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import com.digitalmoneyhouse.iamservice.dto.UserAccountResponse;
import com.digitalmoneyhouse.iamservice.exception.BusinessException;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountServiceservice;

    @PostMapping
    public ResponseEntity<GenericSucessResponse> save(@Valid @RequestBody UserAccountBody user) throws BusinessException {
        return ResponseEntity.status(201).body(userAccountServiceservice.save(user));
    }

    @PostMapping("/confirm-registration")
    public ResponseEntity<UserAccountResponse> confirmRegistration(@RequestBody ConfirmRegistration confirmRegistration) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(userAccountServiceservice.confirmRegistration(confirmRegistration));
    }

}
