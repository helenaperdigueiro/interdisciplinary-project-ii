package com.digitalmoneyhouse.iamservice.exception;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(401, "Incorrect E-mail or Password. Please check your details and try again.");
    }
}
