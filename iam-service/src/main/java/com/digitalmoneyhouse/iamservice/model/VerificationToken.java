package com.digitalmoneyhouse.iamservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
    private static final Long EXPIRATION_IN_MINUTES = 60L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String verificationCode;

    @OneToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_account_id")
    private UserAccount userAccount;

    private LocalDateTime expiryDate;

    public VerificationToken(UserAccount userAccount) {
        this.verificationCode = generateVerificationCode();
        this.userAccount = userAccount;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_IN_MINUTES);
    }

    public String generateVerificationCode() {
        Random generator = new Random();
        return String.valueOf(generator.nextInt(100000,1000000));
    }
}