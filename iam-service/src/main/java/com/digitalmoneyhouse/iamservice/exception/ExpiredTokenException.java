package com.digitalmoneyhouse.iamservice.exception;

public class ExpiredTokenException extends BusinessException {
    public ExpiredTokenException() {
        super(401, "Your session has expired. Please login again to continue.");
    }
}
