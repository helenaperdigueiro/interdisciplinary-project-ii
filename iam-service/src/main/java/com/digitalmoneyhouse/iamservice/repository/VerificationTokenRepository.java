package com.digitalmoneyhouse.iamservice.repository;

import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    Boolean existsByVerificationCodeAndUserAccountEmail(String verificationCode, String email);
    VerificationToken findByVerificationCode(String verificationCode);
}
