package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.*;
import com.digitalmoneyhouse.iamservice.exception.BusinessException;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<GenericSucessResponse> save(@Valid @RequestBody UserAccountBody user) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAccountService.save(user));
    }

    @PostMapping("/confirm-registration")
    public ResponseEntity<UserAccountResponse> confirmRegistration(@RequestBody ConfirmRegistration confirmRegistration) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(userAccountService.confirmRegistration(confirmRegistration));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> findById(@PathVariable Integer id) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(userAccountService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserAccountResponse> editById(@PathVariable Integer id, @RequestBody UserAccountPatch userAccountPatch) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(userAccountService.editById(id, userAccountPatch));
    }
}
