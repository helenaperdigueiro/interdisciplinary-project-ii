package com.digitalmoneyhouse.iamservice.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(404, "User not found");
    }
}
