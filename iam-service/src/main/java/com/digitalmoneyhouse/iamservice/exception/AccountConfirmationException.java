package com.digitalmoneyhouse.iamservice.exception;

public class AccountConfirmationException extends BusinessException {
    public AccountConfirmationException() {
        super(400, "Account not found or the given verification code has expired or is invalid.");
    }
}
