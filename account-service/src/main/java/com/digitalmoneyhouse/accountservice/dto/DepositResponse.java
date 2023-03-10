package com.digitalmoneyhouse.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepositResponse extends TransactionResponse {
    private Integer cardId;
    private Integer accountId;
}

