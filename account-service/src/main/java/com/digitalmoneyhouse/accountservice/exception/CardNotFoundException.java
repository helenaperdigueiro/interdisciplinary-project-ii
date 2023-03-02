package com.digitalmoneyhouse.accountservice.exception;

public class CardNotFoundException extends BusinessException {
    public CardNotFoundException() {
        super(404, "Card not found");
    }
}
