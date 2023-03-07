package com.digitalmoneyhouse.accountservice.model;

import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer cardId;

    @Column(nullable = false)
    private Integer accountId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private TransactionType type;

    private String description;


    public Transaction(TransactionRequest transactionRequest, Integer accountId) {
        this.cardId = transactionRequest.getCardId();
        this.accountId = accountId;
        this.amount = transactionRequest.getAmount();
        this.date = LocalDateTime.now();
        this.type = Objects.requireNonNullElse(type, TransactionType.CASH_DEPOSIT);
        this.description = transactionRequest.getDescription();
    }
}
