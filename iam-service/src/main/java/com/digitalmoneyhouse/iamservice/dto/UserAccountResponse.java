package com.digitalmoneyhouse.iamservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAccountResponse {
    private Integer id;
    private String name;
    private String lastName;
    private String email;
    private String cpf;
    private String phoneNumber;
    private String cvu;
    private String alias;

}
