package com.digitalmoneyhouse.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptContainer {
    private byte[] bytes;
    private String transactionCode;
}
