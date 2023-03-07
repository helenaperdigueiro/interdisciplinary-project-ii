package com.digitalmoneyhouse.iamservice.dto;

import com.digitalmoneyhouse.iamservice.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfile {
    private String firstName;
    private String lastName;
    private String cpf;
    private String phoneNumber;
    private String email;

    public UserProfile(UserAccount user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.cpf = user.getCpf();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
    }
}
