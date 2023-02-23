package com.digitalmoneyhouse.iamservice.repository;

import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    UserAccount findByEmail(String email);
    UserAccount findByEmailAndIsEnabled(String email, boolean isEnabled);
    Boolean existsByCpf(String cpf);
    Boolean existsByEmail(String email);
    @Query(value = "SELECT * FROM digital_money_house.user_account JOIN digital_money_house.password_reset_token ON user_account.id = user_account_id WHERE password_reset_token.token = ?1", nativeQuery = true)
    UserAccount findByToken(String token);
}
