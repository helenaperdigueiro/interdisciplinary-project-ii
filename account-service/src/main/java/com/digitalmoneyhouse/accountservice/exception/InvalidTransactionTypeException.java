package com.digitalmoneyhouse.accountservice.exception;

public class InvalidTransactionTypeException extends BusinessException {
    public InvalidTransactionTypeException() {
        super(400, "Invalid transaction type");
    }
}
