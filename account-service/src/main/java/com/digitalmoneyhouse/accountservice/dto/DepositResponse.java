package com.digitalmoneyhouse.accountservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DepositResponse extends TransactionResponse {
    private Integer cardId;
    private String cardNumber;
    private Integer accountId;
    private String accountNumber;
}

