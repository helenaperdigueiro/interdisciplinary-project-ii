package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import com.digitalmoneyhouse.iamservice.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public VerificationToken create(UserAccount userAccount, String verificationToken) {
        VerificationToken token = new VerificationToken(verificationToken, userAccount);
        return verificationTokenRepository.save(token);
    }
}
