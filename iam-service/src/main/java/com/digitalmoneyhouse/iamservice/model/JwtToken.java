package com.digitalmoneyhouse.iamservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {

//    @Column(length = 255, nullable = false)
//    private String token_id;

    @Id
    @Column(length = 255, nullable = false)
    private String token;


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer authentication_id;
//    @Column(length = 255, nullable = false)
//    private String user_name;
//    @Column(length = 255, nullable = false)
//    private String client_id;
//    @Column(length = 255, nullable = false)
//    private String authentication;
//    @Column(length = 255, nullable = false)
//    private String refresh_token;

}
