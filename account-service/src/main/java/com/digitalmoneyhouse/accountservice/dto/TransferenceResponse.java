package com.digitalmoneyhouse.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenceResponse extends TransactionResponse {
    private String originAccount;
    private String destinationAccount;
}

