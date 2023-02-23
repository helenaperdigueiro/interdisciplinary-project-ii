package com.digitalmoneyhouse.iamservice.exception;

public class EmailAlreadyInUseException extends BusinessException {
    private String email;
    public EmailAlreadyInUseException(String email) {
        super(400, String.format("Value %s for field 'email' is already in use", email));
    }
}
