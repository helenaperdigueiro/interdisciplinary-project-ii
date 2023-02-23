package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailService {

    private static final String NOREPLY_ADDRESS = "no-reply@dhmoney.com";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Value("${api.baseUrl}")
    private String apiBaseUrl;

    public void sendSimpleMessage(SimpleMailMessage simpleMailMessage) {
        try {
            simpleMailMessage.setFrom(NOREPLY_ADDRESS);
            mailSender.send(simpleMailMessage);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    public void sendAccountConfirmationCode(VerificationToken verificationToken) {
        try {
            UserAccount userAccount = verificationToken.getUserAccount();
            String emailTo = userAccount.getEmail();
            String userFirstName = userAccount.getFirstName();
            String verificationCode = verificationToken.getVerificationCode();
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(NOREPLY_ADDRESS);
            simpleMailMessage.setTo(emailTo);
            simpleMailMessage.setSubject("Account confirmation");
            simpleMailMessage.setText(String.format(
                    "Dear %s,\n" +
                            "Please use the code below to confirm your account.\n" +
                            "%s", userFirstName, verificationCode
            ));
            mailSender.send(simpleMailMessage);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    public void sendResetPasswordLink(PasswordResetToken passwordResetToken) {
        try {
            UserAccount userAccount = passwordResetToken.getUserAccount();
            String emailTo = userAccount.getEmail();
            String userFirstName = userAccount.getFirstName();
            String token = passwordResetToken.getToken();
            String link = apiBaseUrl + "/update-password?token=" + token;
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(NOREPLY_ADDRESS);
            simpleMailMessage.setTo(emailTo);
            simpleMailMessage.setSubject("Reset Password");
            simpleMailMessage.setText(String.format(
                    "Dear %s,\n" +
                    "Please click the link below to reset your password.\n" +
                    "%s", userFirstName, link
            ));
            mailSender.send(simpleMailMessage);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }
}
