package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.exception.InvalidTokenException;
import com.digitalmoneyhouse.iamservice.model.JwtToken;
import com.digitalmoneyhouse.iamservice.repository.JwtTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    @Autowired
    private JwtTokenRepository repository;

    public Boolean isValid(String token) {
        return repository.findByToken(token) != null;
    }

    public Boolean save(String token){
        JwtToken jwtToken = new JwtToken(token);
        repository.save(jwtToken);
        return true;
    }

    public void delete(String token) throws InvalidTokenException {
        JwtToken jwtToken = new JwtToken(token);
        if (!isValid(token)) {
            throw new InvalidTokenException();
        };
        repository.delete(jwtToken);
    }
}
