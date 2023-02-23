package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.ConfirmRegistration;
import com.digitalmoneyhouse.iamservice.dto.GenericSucessResponse;
import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import com.digitalmoneyhouse.iamservice.dto.UserAccountResponse;
import com.digitalmoneyhouse.iamservice.exception.BusinessException;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserAccountController {
    @Autowired
    private UserAccountService service;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @PostMapping
    public ResponseEntity<GenericSucessResponse> save(@Valid @RequestBody UserAccountBody user) throws BusinessException {
        VerificationToken verificationToken = service.save(user);
        mailSender.send(constructConfirmationEmail(verificationToken));

        return ResponseEntity.status(201).body(new GenericSucessResponse("Please confirm your account"));
    }

    @PostMapping("/confirm-registration")
    public ResponseEntity<UserAccountResponse> confirmRegistration(@RequestBody ConfirmRegistration confirmRegistration) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(service.confirmRegistration(confirmRegistration));
    }

    private SimpleMailMessage constructConfirmationEmail(VerificationToken verificationToken) {
        UserAccount userAccount = verificationToken.getUserAccount();
        return constructEmail("Account confirmation", "CONFIRM YOUR REGISTRATION" + " \r\n" + verificationToken.getVerificationCode(), userAccount);
    }
    private SimpleMailMessage constructEmail(String subject, String body, UserAccount userAccount) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(userAccount.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

}
