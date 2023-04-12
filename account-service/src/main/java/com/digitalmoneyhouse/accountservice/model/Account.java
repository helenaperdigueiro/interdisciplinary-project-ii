package com.digitalmoneyhouse.accountservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ACCOUNTS")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String userCpf;

    @Column(nullable = false)
    private String  userFullName;

    private String accountNumber;

    private Double walletBalance;
}
