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
@Table(name = "TRANSFERENCES")
public class Transference extends Transaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id")
    private Account originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    public Transference(TransactionRequest request, Account originAccount, Account destinationAccount) {
        super(request.getAmount(), TransactionType.CASH_TRANSFERENCE, request.getDescription());
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
    }
}
