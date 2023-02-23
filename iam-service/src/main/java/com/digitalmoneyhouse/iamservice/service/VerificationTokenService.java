package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import com.digitalmoneyhouse.iamservice.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public VerificationToken create(UserAccount userAccount) {
        VerificationToken verificationToken = new VerificationToken(userAccount);
        return verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken findByVerificationCode(String verificationCode) {
        return verificationTokenRepository.findByVerificationCode(verificationCode);
    }

    public boolean existsByVerificationCodeAndUserAccountEmail(String verificationCode, String email) {
        return verificationTokenRepository.existsByVerificationCodeAndUserAccountEmail(verificationCode, email);
    }

    public void deleteById(Integer id) {
        verificationTokenRepository.deleteById(id);
    }
}
