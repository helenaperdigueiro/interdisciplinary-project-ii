package com.digitalmoneyhouse.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private Integer id;
    private Double amount;
    private LocalDateTime date;
    private String type;
    private String description;

}
