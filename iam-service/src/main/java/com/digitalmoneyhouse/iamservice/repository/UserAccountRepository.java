package com.digitalmoneyhouse.iamservice.repository;

import com.digitalmoneyhouse.iamservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    UserAccount findByEmail(String email);
}
