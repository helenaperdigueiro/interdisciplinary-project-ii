package com.digitalmoneyhouse.iamservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmRegistration {
    private String email;
    private String verificationCode;
}
