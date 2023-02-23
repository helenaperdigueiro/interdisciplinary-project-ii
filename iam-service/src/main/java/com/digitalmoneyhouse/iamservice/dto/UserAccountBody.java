package com.digitalmoneyhouse.iamservice.dto;

import com.digitalmoneyhouse.iamservice.util.BrazilianCellPhone;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
    //@CPF
    @NotBlank
    private String cpf;
    @BrazilianCellPhone
    private String phoneNumber;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
}
