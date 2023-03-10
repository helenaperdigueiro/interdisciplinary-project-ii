package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT a.accountNumber FROM Account a WHERE a.id = :id")
    String findAccountNumberById(@Param("id") Integer accountId);
}
