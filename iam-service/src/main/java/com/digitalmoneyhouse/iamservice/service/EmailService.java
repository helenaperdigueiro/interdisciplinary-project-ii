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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            UserAccount userAccount = verificationToken.getUserAccount();
            String emailTo = userAccount.getEmail();
            String userFirstName = userAccount.getFirstName();
            String verificationCode = verificationToken.getVerificationCode();

            String htmlBody = String.format(
                    "<body style=\"background-color: #333333; color: #f2f2f2; font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; margin: 0; padding: 0; text-align: center;\">\n" +
                    "\t<div class=\"container\" style=\"display: flex; flex-direction: column; align-items: center; height: 100vh;\">\n" +
                    "\t\t<h1 style=\"margin-top: 10;\">Account Confirmation</h1>\n" +
                    "\t\t<p style=\"margin: 20px 0;\">Dear, %s</p>\n" +
                    "\t\t<p style=\"margin: 20px 0;\">Please use the code below to confirm your account</p>\n" +
                    "\t\t<p style=\"background-color: #BEF738; border: none; border-radius: 5px; color: #202022; display: inline-block; padding: 10px; text-decoration: none;\">%s</p>\n" +
                    "\t</div>\n" +
                    "</body>", userFirstName, verificationCode
            );
            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(emailTo);
            helper.setSubject("Account Confirmation");
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException exception) {
            exception.printStackTrace();
        }
    }

    public void sendResetPasswordLink(PasswordResetToken passwordResetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            UserAccount userAccount = passwordResetToken.getUserAccount();
            String emailTo = userAccount.getEmail();
            String userFirstName = userAccount.getFirstName();
            String token = passwordResetToken.getToken();
            String link = apiBaseUrl + "/update-password?token=" + token;
            String htmlBody =  String.format(
                    "<body style=\"background-color: #333333; color: #f2f2f2; font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5; margin: 0; padding: 0; text-align: center;\">\n" +
                    "\t<div class=\"container\" style=\"display: flex; flex-direction: column; align-items: center; height: 100vh;\">\n" +
                    "\t\t<h1 style=\"margin-top: 10;\">Password Recovery</h1>\n" +
                    "\t\t<p style=\"margin: 20px 0;\">Dear, %s</p>\n" +
                    "\t\t<p style=\"margin: 20px 0;\">We received a request to reset the password for your account. To reset your password, click on the link below.</p>\n" +
                    "\t\t<a href=\"%s\" style=\"background-color: #BEF738; border: none; border-radius: 5px; color: #202022; display: inline-block; padding: 10px; text-decoration: none; cursor: pointer;\">Reset password</a>\n" +
                    "\t\t<p style=\"margin: 20px 0;\">Or copy and paste the URL into your browser:</p>\n" +
                    "\t\t<p style=\"color: #ffffff;\">%s</p>\n" +
                    "\t\t<p style=\"margin: 20px 0;\">If you didn't request a password reset, you can ignore this email. Your password will not be changed.</p>\n" +
                    "\t</div>\n" +
                    "</body>", userFirstName, link, link
            );
            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(emailTo);
            helper.setSubject("Reset Password");
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
