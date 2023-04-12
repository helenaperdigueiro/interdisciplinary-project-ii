package com.digitalmoneyhouse.iamservice.exception;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(401, "Account not verified or Incorrect E-mail or Incorrect Password. Please check your details and try again.");
    }
}
