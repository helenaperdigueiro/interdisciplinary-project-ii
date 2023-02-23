package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.exception.ResetPasswordTokenInvalidOrExpiredException;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public void createPasswordResetTokenForUser(UserAccount userAccount, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, userAccount);
        passwordResetTokenRepository.save(myToken);
    }

    public String validatePasswordResetToken(String token) throws ResetPasswordTokenInvalidOrExpiredException {
        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null || isTokenExpired(passwordResetToken)) {
            throw new ResetPasswordTokenInvalidOrExpiredException();
        }

        return token;
    }
    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    public void deleteToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
