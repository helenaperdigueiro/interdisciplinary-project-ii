package com.digitalmoneyhouse.iamservice.model;

import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Random;

@Entity
@Getter @Setter
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String firstName;

    @Column(length = 100, nullable = false)
    private String lastName;

    @Column(length = 100, nullable = false)
    private String cpf;

    @Column(length = 100, nullable = false)
    private String phoneNumber;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    private String cvu;

    private String alias;

    public UserAccount(UserAccountBody user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.cpf = user.getCpf();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.cvu = generateCVU();
        this.alias = generateAlias();
    }

    public String generateCVU() {
        Random random = new Random();
        String cvu = "";
        for (int i = 0; i < 22; i++) {
            cvu += random.nextInt(10);
        }
        return cvu;
    }

    public String generateAlias() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return StringUtils.trimAllWhitespace(this.getFirstName() + this.getLastName() + randomNumber);
    }
}
