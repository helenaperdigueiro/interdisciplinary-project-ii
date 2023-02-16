package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.PasswordDto;
import com.digitalmoneyhouse.iamservice.mail.GenericResponse;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.security.AuthenticationRequest;
import com.digitalmoneyhouse.iamservice.security.JwtUtil;
import com.digitalmoneyhouse.iamservice.service.JwtTokenService;
import com.digitalmoneyhouse.iamservice.service.PasswordService;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private PasswordService passwordService;

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
    public String revokeToken(@PathVariable String tokenId) {
       jwtTokenService.delete(tokenId);
           return tokenId;
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
            throw new BadCredentialsException("Incorrect", e);
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
    public GenericResponse resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) throws Exception {
        UserAccount user = userAccountService.findByEmail(userEmail);
        if (user == null) {
            throw new Exception("User not found");
        }
        String token = UUID.randomUUID().toString();
        passwordService.createPasswordResetTokenForUser(user, token);
        mailSender.send(constructResetTokenEmail(apiBaseUrl,
                request.getLocale(), token, user));
//        return new GenericResponse(
//                messages.getMessage("RESETE A SENHA", null,
//                        request.getLocale()));
        return new GenericResponse("RESETE A SENHA");
    }

    @PostMapping("/user/changePassword")
    public GenericResponse showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token,
                                         @RequestBody PasswordDto passwordDto) {
        String result = passwordService.validatePasswordResetToken(token);

        if(result != null) {
//            return new GenericResponse(messages.getMessage(
//                    "auth.message." + result, null, locale));
            return new GenericResponse("banana");
        }
        PasswordResetToken passwordResetToken = passwordService.findByToken(token);
        UserAccount user = userAccountService.getUserByPasswordResetToken(passwordResetToken);
        //if(user.isPresent()) {
            userAccountService.changeUserPassword(user, passwordDto.getNewPassword());
            passwordService.deleteToken(passwordResetToken);
            return new GenericResponse("Senha atualizada");
//            return new GenericResponse(messages.getMessage(
//                    "message.resetPasswordSuc", null, locale));
//        } else {
//            return new GenericResponse(messages.getMessage(
//                    "auth.message.invalid", null, locale));
//        }
    }

    private SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, UserAccount userAccount) {
        String url = contextPath + "/user/changePassword?token=" + token;
//        String message = messages.getMessage("message.resetPassword",
//                null, locale);
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
