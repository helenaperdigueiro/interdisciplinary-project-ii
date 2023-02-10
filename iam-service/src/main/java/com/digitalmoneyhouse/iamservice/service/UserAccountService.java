package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import com.digitalmoneyhouse.iamservice.dto.UserAccountResponse;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserAccountRepository repository;

    public UserAccountResponse save(UserAccountBody userAccountBody) {
        String encryptedPassword = bCryptPasswordEncoder.encode(userAccountBody.getPassword());
        userAccountBody.setPassword(encryptedPassword);
        UserAccount userModel = new UserAccount(userAccountBody);
        userModel = repository.save(userModel);
        return new UserAccountResponse(userModel);
    }

}
