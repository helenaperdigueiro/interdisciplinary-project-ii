package com.digitalmoneyhouse.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRequest {

    private String number;

    private String holder;

    private String expirationDate;

    private String cvc;

}
