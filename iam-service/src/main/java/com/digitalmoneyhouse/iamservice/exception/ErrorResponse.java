package com.digitalmoneyhouse.iamservice.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private Integer status;
    private String message;
    private Map<String, String> extra;
}
