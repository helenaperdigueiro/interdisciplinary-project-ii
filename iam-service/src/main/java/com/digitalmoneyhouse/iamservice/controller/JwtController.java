package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.dto.GenericSucessResponse;
import com.digitalmoneyhouse.iamservice.dto.PasswordDto;
import com.digitalmoneyhouse.iamservice.exception.BusinessException;
import com.digitalmoneyhouse.iamservice.exception.InvalidCredentialsException;
import com.digitalmoneyhouse.iamservice.exception.InvalidTokenException;
import com.digitalmoneyhouse.iamservice.model.JwtToken;
import com.digitalmoneyhouse.iamservice.security.AuthenticationRequest;
import com.digitalmoneyhouse.iamservice.security.JwtUtil;
import com.digitalmoneyhouse.iamservice.service.JwtTokenService;
import com.digitalmoneyhouse.iamservice.service.PasswordResetTokenService;
import com.digitalmoneyhouse.iamservice.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @RequestMapping(method = RequestMethod.POST, value = "/tokens/revoke/{tokenId:.*}")
    @ResponseBody
    public GenericSucessResponse revokeToken(@PathVariable String tokenId) throws InvalidTokenException {
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
        tokenResponse.put("accessToken", jwt);
        jwtTokenService.save(jwt);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericSucessResponse> resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail) {
        return ResponseEntity.status(HttpStatus.OK).body(userAccountService.resetPassword(userEmail));
    }

    @PutMapping("/update-password")
    public ResponseEntity<GenericSucessResponse> updatePassword(
            @RequestParam("token") String token,
            @RequestBody PasswordDto passwordDto
    ) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(userAccountService.changeUserPassword(token, passwordDto));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Void> validateToken(@RequestBody JwtToken token) {
        Boolean isValid = jwtUtil.validateToken(token);
        System.out.println(token);
        System.out.println(isValid);
        return isValid ?
                ResponseEntity.status(HttpStatus.OK).build() : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
