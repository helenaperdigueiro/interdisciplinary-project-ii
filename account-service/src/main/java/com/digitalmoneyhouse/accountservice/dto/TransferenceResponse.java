package com.digitalmoneyhouse.accountservice.dto;

import com.digitalmoneyhouse.accountservice.model.Transference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferenceResponse extends TransactionResponse {
    private String originAccountNumber;
    private String originAccountHolderName;
    private String destinationAccountNumber;
    private String destinationAccountHolderName;

    public TransferenceResponse(Transference transference) {
        super(transference.getId(), transference.getAmount(), transference.getDate(), transference.getType().toString(), transference.getTransactionCode(), transference.getDescription());
        this.originAccountNumber = transference.getOriginAccount().getAccountNumber();
        this.originAccountHolderName = transference.getOriginAccount().getUserFullName();
        this.destinationAccountNumber = transference.getDestinationAccount().getAccountNumber();
        this.destinationAccountHolderName = transference.getDestinationAccount().getUserFullName();
    }
}

