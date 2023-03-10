package com.digitalmoneyhouse.accountservice.exception;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException() {
        super(400, "Insufficient Balance in account");
    }
}
