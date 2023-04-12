package com.digitalmoneyhouse.iamservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAccountPatch {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String password;
}
