package com.digitalmoneyhouse.accountservice.exception;

public class AuthorizationHeaderNotFoundException extends BusinessException {
    public AuthorizationHeaderNotFoundException() {
        super(400, "Bearer token is required in the Authorization header");
    }
}
