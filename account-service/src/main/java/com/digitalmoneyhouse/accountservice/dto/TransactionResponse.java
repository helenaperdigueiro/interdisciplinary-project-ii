package com.digitalmoneyhouse.accountservice.dto;

import com.digitalmoneyhouse.accountservice.model.Transaction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransactionResponse {
    private Integer id;
    private Double amount;
    private LocalDateTime date;
    private String type;
    private String transactionCode;
    private String description;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
        this.type = transaction.getType().toString();
        this.transactionCode = transaction.getTransactionCode();
        this.description = transaction.getDescription();
    }
}
