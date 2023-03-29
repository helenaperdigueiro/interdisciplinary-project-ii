package com.digitalmoneyhouse.accountservice.exception;

import com.digitalmoneyhouse.accountservice.model.TransactionType;

import java.util.ArrayList;
import java.util.EnumSet;

public class InvalidTransactionTypeException extends BusinessException {
    public InvalidTransactionTypeException() {
        super(400, String.format("Invalid value for param 'type', accepted: %s", new ArrayList<TransactionType>(EnumSet.allOf(TransactionType.class))));
    }
}
