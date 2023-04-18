package com.digitalmoneyhouse.accountservice.dto;

import java.math.BigDecimal;

import com.digitalmoneyhouse.accountservice.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    private String description;

    private Integer cardId;

    private String destinationAccount;

    public TransactionRequest(BigDecimal amount, TransactionType type, String destinationAccount) {
        this.amount = amount;
        this.type = type;
        this.destinationAccount = destinationAccount;
    }

    public TransactionRequest(BigDecimal amount, TransactionType type, String description, String destinationAccount) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.destinationAccount = destinationAccount;
    }

    public TransactionRequest(BigDecimal amount, TransactionType type, Integer cardId) {
        this.amount = amount;
        this.type = type;
        this.cardId = cardId;
    }

    public TransactionRequest(BigDecimal amount, TransactionType type, String description, Integer cardId) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.cardId = cardId;
    }

}
