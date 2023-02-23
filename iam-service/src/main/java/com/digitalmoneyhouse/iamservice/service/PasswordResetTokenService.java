package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.dto.GenericSucessResponse;
import com.digitalmoneyhouse.iamservice.exception.ResetPasswordTokenInvalidOrExpiredException;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    public GenericSucessResponse reset(UserAccount userAccount) {
        GenericSucessResponse response = new GenericSucessResponse(
                "If there's an account associated with the informed e-mail we'll send you a link to reset the password."
        );
        if (userAccount == null) {
            return response;
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken(userAccount);
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendResetPasswordLink(passwordResetToken);
        return response;
    }

    public PasswordResetToken validatePasswordResetToken(String token) throws ResetPasswordTokenInvalidOrExpiredException {
        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null || isTokenExpired(passwordResetToken)) {
            throw new ResetPasswordTokenInvalidOrExpiredException();
        }

        return passwordResetToken;
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
