package com.digitalmoneyhouse.accountservice.model;

import com.digitalmoneyhouse.accountservice.dto.CardRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String number;

    private String holder;

    private String expirationDate;

    private String cvc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Account account;

    public Card(CardRequest cardRequest) {
        this.number = cardRequest.getNumber();
        this.holder = cardRequest.getHolder();
        this.expirationDate = cardRequest.getExpirationDate();
        this.cvc = cardRequest.getCvc();
    }

}
