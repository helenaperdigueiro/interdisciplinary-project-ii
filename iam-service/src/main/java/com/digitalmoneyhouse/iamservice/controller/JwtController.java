package com.digitalmoneyhouse.iamservice.controller;

import com.digitalmoneyhouse.iamservice.repository.TokenRepository;
import com.digitalmoneyhouse.iamservice.security.AuthenticationRequest;
import com.digitalmoneyhouse.iamservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JwtController {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{
        System.out.println(authenticationRequest.getEmail());
        System.out.println(authenticationRequest.getPassword());

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
        return ResponseEntity.ok(tokenResponse);
    }

}
