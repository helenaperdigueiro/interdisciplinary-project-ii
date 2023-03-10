package com.digitalmoneyhouse.accountservice.model;

import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

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
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    public Transaction(Double amount, TransactionType type, String description) {
        this.amount = amount;
        this.type = type;
        this.date = LocalDateTime.now();
        this.description = description;
    }
}
