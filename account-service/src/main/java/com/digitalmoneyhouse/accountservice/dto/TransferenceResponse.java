package com.digitalmoneyhouse.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenceResponse extends TransactionResponse {
    private String originAccountNumber;
    private String originAccountHolderName;
    private String destinationAccountNumber;
    private String destinationAccountHolderName;
}

