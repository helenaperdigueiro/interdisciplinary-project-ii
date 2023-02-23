package com.digitalmoneyhouse.iamservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    private static final Long EXPIRATION_IN_MINUTES = 60L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @OneToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_account_id")
    private UserAccount userAccount;

    private LocalDateTime expiryDate;

    public PasswordResetToken(UserAccount userAccount) {
        this.token = generateToken();
        this.userAccount = userAccount;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_IN_MINUTES);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
