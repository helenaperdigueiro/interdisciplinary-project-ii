package com.digitalmoneyhouse.iamservice.exception;

public class ResetPasswordTokenInvalidOrExpiredException extends BusinessException {
    public ResetPasswordTokenInvalidOrExpiredException() {
        super(400, "Invalid or expired link.");
    }
}
