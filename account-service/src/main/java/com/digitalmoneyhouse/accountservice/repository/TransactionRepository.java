package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "SELECT\n" +
            "\ttransactions.id,\n" +
            "    transactions.amount,\n" +
            "    transactions.date,\n" +
            "    transactions.description,\n" +
            "    transactions.type,\n" +
            "    deposits.card_id,\n" +
            "    deposits.account_id,\n" +
            "    transferences.origin_account_id,\n" +
            "    transferences.destination_account_id\n" +
            "FROM transactions\n" +
            "LEFT JOIN deposits\n" +
            "ON transactions.id = deposits.id \n" +
            "LEFT JOIN transferences\n" +
            "ON transactions.id = transferences.id\n" +
            "WHERE deposits.account_id = ?1 OR transferences.origin_account_id = ?1 OR transferences.destination_account_id = ?1\n" +
            "ORDER BY date DESC\n" +
            "LIMIT ?2", nativeQuery = true)
    List<Object[]> findALlByAccountId(Integer accountId, Integer limit);

    @Query(value = "SELECT\n" +
            "\ttransactions.id,\n" +
            "    transactions.amount,\n" +
            "    transactions.date,\n" +
            "    transactions.description,\n" +
            "    transactions.type,\n" +
            "    deposits.card_id,\n" +
            "    deposits.account_id,\n" +
            "    transferences.origin_account_id,\n" +
            "    transferences.destination_account_id\n" +
            "FROM transactions\n" +
            "LEFT JOIN deposits\n" +
            "ON transactions.id = deposits.id \n" +
            "LEFT JOIN transferences\n" +
            "ON transactions.id = transferences.id\n" +
            "WHERE (deposits.account_id = ?1 OR transferences.origin_account_id = ?1 OR transferences.destination_account_id = ?1)\n" +
            "AND type = ?2\n" +
            "ORDER BY date DESC\n" +
            "LIMIT ?3", nativeQuery = true)
    List<Object[]> findALlByAccountIdAndType(Integer accountId, String type, Integer limit);
}
