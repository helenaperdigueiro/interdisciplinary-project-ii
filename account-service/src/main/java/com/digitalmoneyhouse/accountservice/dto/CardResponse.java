package com.digitalmoneyhouse.accountservice.dto;

import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.util.Formatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardResponse {

    private Integer id;
    private String number;
    private String holder;
    private String expirationDate;
    private String cvc;
    private Integer accountId;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.number = Formatter.maskCardNumber(card.getNumber());
        this.holder = card.getHolder();
        this.expirationDate = card.getExpirationDate();
        this.cvc = card.getCvc();
        this.accountId = card.getAccount().getId();
    }
}
