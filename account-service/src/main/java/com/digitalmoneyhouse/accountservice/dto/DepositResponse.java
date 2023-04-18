package com.digitalmoneyhouse.accountservice.dto;

import com.digitalmoneyhouse.accountservice.model.Deposit;
import com.digitalmoneyhouse.accountservice.util.Formatter;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DepositResponse extends TransactionResponse {
    private Integer cardId;
    private String cardNumber;
    private Integer accountId;
    private String accountNumber;

    public DepositResponse(Deposit deposit, String cardNumber) {
        super(deposit.getId(), deposit.getAmount(), deposit.getDate(), deposit.getType().toString(), deposit.getTransactionCode(), deposit.getDescription());
        this.cardId = deposit.getCardId();
        this.cardNumber = Formatter.maskCardNumber(cardNumber);
        this.accountId = deposit.getAccount().getId();
        this.accountNumber = deposit.getAccount().getAccountNumber();
    }
}

