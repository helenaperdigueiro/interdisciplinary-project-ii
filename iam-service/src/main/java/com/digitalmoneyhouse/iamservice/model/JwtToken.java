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

    @Id
    @Column(length = 255, nullable = false)
    private String token;

}
