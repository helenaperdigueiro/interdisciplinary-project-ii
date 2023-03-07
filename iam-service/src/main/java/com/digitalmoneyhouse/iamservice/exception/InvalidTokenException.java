package com.digitalmoneyhouse.iamservice.exception;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super(401, "Invalid token");
    }
}
