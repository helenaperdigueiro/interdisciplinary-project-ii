package com.digitalmoneyhouse.iamservice.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AuthenticationRequest {
    private String email;
    private String password;
}
