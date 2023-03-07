package com.digitalmoneyhouse.accountservice.exception;

public class DuplicatedCardNumberException extends BusinessException {
    public DuplicatedCardNumberException() {
        super(409, "Card Number already exists");
    }
}
