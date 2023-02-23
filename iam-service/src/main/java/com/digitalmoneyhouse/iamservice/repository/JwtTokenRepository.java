package com.digitalmoneyhouse.iamservice.repository;

import com.digitalmoneyhouse.iamservice.model.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {
    JwtToken findByToken(String token);
}
