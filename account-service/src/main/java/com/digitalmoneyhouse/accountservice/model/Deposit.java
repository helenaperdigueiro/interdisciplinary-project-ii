package com.digitalmoneyhouse.accountservice.model;

import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DEPOSITS")
public class Deposit extends Transaction {

    @Column(nullable = false)
    private Integer cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Deposit(TransactionRequest request, Account account) {
        super(request.getAmount(), TransactionType.CASH_DEPOSIT, request.getDescription());
        this.cardId = request.getCardId();
        this.account = account;
    }
}
