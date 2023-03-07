package com.digitalmoneyhouse.accountservice.dto;

import com.digitalmoneyhouse.accountservice.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRequest {

    @NotBlank
    private Integer cardId;

    @NotBlank
    private Double amount;

    private TransactionType type;

    private String description;

    public TransactionRequest(Integer cardId, Double amount, TransactionType type) {
        this.cardId = cardId;
        this.amount = amount;
        this.type = Objects.requireNonNullElse(type, TransactionType.CASH_DEPOSIT);
    }

    public TransactionRequest(Integer cardId, Double amount, TransactionType type, String description) {
        this.cardId = cardId;
        this.amount = amount;
        this.type = Objects.requireNonNullElse(type, TransactionType.CASH_DEPOSIT);
        this.description = description;
    }

}
