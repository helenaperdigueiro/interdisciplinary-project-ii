package com.digitalmoneyhouse.accountservice.exception;

public class InvalidTypeException extends BusinessException {
    public InvalidTypeException() {
        super(400, "Invalid type exception");
    }
}
