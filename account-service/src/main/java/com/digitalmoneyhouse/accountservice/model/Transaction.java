package com.digitalmoneyhouse.accountservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TRANSACTIONS")
@Inheritance(strategy = InheritanceType.JOINED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private String transactionCode;
    private String description;

    public Transaction(BigDecimal amount, TransactionType type, String description) {
        this.amount = amount;
        this.type = type;
        this.date = LocalDateTime.now();
        this.transactionCode = UUID.randomUUID().toString().toUpperCase();
        this.description = description;
    }
}
