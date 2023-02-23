package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.GenericSucessResponse;
import com.digitalmoneyhouse.iamservice.dto.PasswordDto;
import com.digitalmoneyhouse.iamservice.exception.BusinessException;
import com.digitalmoneyhouse.iamservice.exception.InvalidCredentialsException;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.security.AuthenticationRequest;
import com.digitalmoneyhouse.iamservice.security.JwtUtil;
import com.digitalmoneyhouse.iamservice.service.JwtTokenService;
import com.digitalmoneyhouse.iamservice.service.PasswordResetTokenService;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Autowired
    private MessageSource messages;

    @Value("${api.baseUrl}")
    private String apiBaseUrl;

    @RequestMapping(method = RequestMethod.POST, value = "/tokens/revoke/{tokenId:.*}")
    @ResponseBody
    public GenericSucessResponse revokeToken(@PathVariable String tokenId) {
       jwtTokenService.delete(tokenId);
           return new GenericSucessResponse("You have been logged out.");
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
        try {
            authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()));

        }catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCredentialsException();
        }
        final UserDetails userDetails =
                userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("acessToken", jwt);
        jwtTokenService.save(jwt);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/reset-password")
    public GenericSucessResponse resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) throws Exception {
        GenericSucessResponse response = new GenericSucessResponse(String.format("If there's an account associated with the e-mail %s we'll send you a link to reset the password.", userEmail));
        UserAccount user = userAccountService.findByEmail(userEmail);
        if (user == null) {
            return response;
        }
        String token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user, token);
        mailSender.send(constructResetTokenEmail(apiBaseUrl,
                request.getLocale(), token, user));
        return response;
    }

    @PostMapping("/user/changePassword")
    public GenericSucessResponse showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token,
                                         @RequestBody PasswordDto passwordDto) throws BusinessException {
        passwordResetTokenService.validatePasswordResetToken(token);

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        UserAccount user = userAccountService.getUserByPasswordResetToken(passwordResetToken);
        userAccountService.changeUserPassword(user, passwordDto.getNewPassword());
        passwordResetTokenService.deleteToken(passwordResetToken);
        return new GenericSucessResponse("Your password has been changed successfully.");
    }

    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, UserAccount userAccount) {
        String url = contextPath + "/user/changePassword?token=" + token;
        return constructEmail("Reset Password", "RESETE A SENHA" + " \r\n" + url, userAccount);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             UserAccount userAccount) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(userAccount.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

}
