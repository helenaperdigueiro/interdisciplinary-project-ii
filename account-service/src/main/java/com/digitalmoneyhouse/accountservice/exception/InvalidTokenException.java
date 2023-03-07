package com.digitalmoneyhouse.accountservice.exception;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super(401, "Invalid token");
    }
}
