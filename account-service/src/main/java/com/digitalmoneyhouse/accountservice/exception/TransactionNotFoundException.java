package com.digitalmoneyhouse.accountservice.exception;

public class TransactionNotFoundException extends BusinessException {
    public TransactionNotFoundException() {
        super(404, "Transaction not found");
    }
}
