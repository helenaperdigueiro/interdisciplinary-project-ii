package com.digitalmoneyhouse.iamservice.service;

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

    public UserAccountResponse save(UserAccount userAccount) {
        String encryptedPassword = bCryptPasswordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(encryptedPassword);
        UserAccount userAccountSaved = repository.save(userAccount);
        UserAccountResponse userAccountResponse = new UserAccountResponse(
                userAccountSaved.getId(),
                userAccountSaved.getName(),
                userAccountSaved.getLastName(),
                userAccountSaved.getEmail(),
                userAccountSaved.getCpf(),
                userAccountSaved.getPhoneNumber(),
                userAccountSaved.getCvu(),
                userAccountSaved.getAlias());
        return userAccountResponse;
    }

}
