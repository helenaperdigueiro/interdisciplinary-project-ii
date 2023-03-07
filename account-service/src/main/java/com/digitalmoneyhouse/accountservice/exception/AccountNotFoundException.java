package com.digitalmoneyhouse.accountservice.exception;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(404, "Account not found");
    }
}
