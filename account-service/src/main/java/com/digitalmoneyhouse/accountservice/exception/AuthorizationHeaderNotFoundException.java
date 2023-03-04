package com.digitalmoneyhouse.accountservice.exception;

public class AuthorizationHeaderNotFoundException extends BusinessException {
    public AuthorizationHeaderNotFoundException() {
        super(400, "You're must send the Bearer token in the Authorization header");
    }
}
