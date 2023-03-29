package com.digitalmoneyhouse.accountservice.exception;

import com.digitalmoneyhouse.accountservice.model.TransactionCategory;

import java.util.ArrayList;
import java.util.EnumSet;

public class InvalidTransactionCategoryException extends BusinessException {
    public InvalidTransactionCategoryException() {
        super(400, String.format("Invalid value for param 'transactionCategory', accepted: %s", new ArrayList<TransactionCategory>(EnumSet.allOf(TransactionCategory.class))));
    }
}
