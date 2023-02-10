package com.digitalmoneyhouse.iamservice.dto;

import com.digitalmoneyhouse.iamservice.util.BrazilianCellPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
public class UserAccountBody {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @CPF
    private String cpf;
    @BrazilianCellPhone
    private String phoneNumber;
    @Email
    private String email;
    @NotBlank
    private String password;
}
