package com.digitalmoneyhouse.iamservice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessException extends Exception{
    private Integer statusCode;
    private String message;

    public BusinessException(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
