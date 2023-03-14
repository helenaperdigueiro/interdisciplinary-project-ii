package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "SELECT\n" +
            "transactions.id,\n" +
            "transactions.amount,\n" +
            "transactions.date,\n" +
            "transactions.type,\n" +
            "transactions.transaction_code,\n" +
            "transactions.description,\n" +
            "deposits.card_id,\n" +
            "cards.number,\n" +
            "deposits.account_id,\n" +
            "accounts.account_number,\n" +
            "transferences.origin_account_id,\n" +
            "transferences.destination_account_id\n" +
            "FROM transactions\n" +
            "LEFT JOIN deposits\n" +
            "ON transactions.id = deposits.id\n" +
            "LEFT JOIN accounts\n" +
            "ON deposits.account_id = accounts.id\n" +
            "LEFT JOIN cards\n" +
            "ON deposits.card_id = cards.id\n" +
            "LEFT JOIN transferences\n" +
            "ON transactions.id = transferences.id\n" +
            "WHERE deposits.account_id = ?1 OR transferences.origin_account_id = ?1 OR transferences.destination_account_id = ?1\n" +
            "ORDER BY date DESC\n" +
            "LIMIT ?2", nativeQuery = true)
    List<Object[]> findALlByAccountId(Integer accountId, Integer limit);

    @Query(value = "SELECT\n" +
            "transactions.id,\n" +
            "transactions.amount,\n" +
            "transactions.date,\n" +
            "transactions.type,\n" +
            "transactions.transaction_code,\n" +
            "transactions.description,\n" +
            "deposits.card_id,\n" +
            "cards.number,\n" +
            "deposits.account_id,\n" +
            "accounts.account_number,\n" +
            "transferences.origin_account_id,\n" +
            "transferences.destination_account_id\n" +
            "FROM transactions\n" +
            "LEFT JOIN deposits\n" +
            "ON transactions.id = deposits.id\n" +
            "LEFT JOIN accounts\n" +
            "ON deposits.account_id = accounts.id\n" +
            "LEFT JOIN cards\n" +
            "ON deposits.card_id = cards.id\n" +
            "LEFT JOIN transferences\n" +
            "ON transactions.id = transferences.id\n" +
            "WHERE (deposits.account_id = ?1 OR transferences.origin_account_id = ?1 OR transferences.destination_account_id = ?1)\n" +
            "AND type = ?2\n" +
            "ORDER BY date DESC\n" +
            "LIMIT ?3", nativeQuery = true)
    List<Object[]> findALlByAccountIdAndType(Integer accountId, String type, Integer limit);

    @Query(value = "SELECT\n" +
            "transactions.id,\n" +
            "transactions.amount,\n" +
            "transactions.date,\n" +
            "transactions.type,\n" +
            "transactions.transaction_code,\n" +
            "transactions.description,\n" +
            "deposits.card_id,\n" +
            "cards.number,\n" +
            "deposits.account_id,\n" +
            "accounts.account_number,\n" +
            "transferences.origin_account_id,\n" +
            "transferences.destination_account_id\n" +
            "FROM transactions\n" +
            "LEFT JOIN deposits\n" +
            "ON transactions.id = deposits.id\n" +
            "LEFT JOIN accounts\n" +
            "ON deposits.account_id = accounts.id\n" +
            "LEFT JOIN cards\n" +
            "ON deposits.card_id = cards.id\n" +
            "LEFT JOIN transferences\n" +
            "ON transactions.id = transferences.id\n" +
            "WHERE transactions.id = ?1\n" +
            "AND (deposits.account_id = ?2 OR transferences.origin_account_id = ?2 OR transferences.destination_account_id = ?2)", nativeQuery = true)
    List<Object[]> findByIdAndAccountId(Integer transactionId, Integer accountID);

    @Query(value = "SELECT\n" +
            "transactions.id,\n" +
            "transactions.amount,\n" +
            "transactions.date,\n" +
            "transactions.type,\n" +
            "transactions.transaction_code,\n" +
            "transactions.description,\n" +
            "deposits.card_id,\n" +
            "cards.number,\n" +
            "deposits.account_id,\n" +
            "accounts.account_number,\n" +
            "transferences.origin_account_id,\n" +
            "transferences.destination_account_id\n" +
            "FROM transactions\n" +
            "LEFT JOIN deposits\n" +
            "ON transactions.id = deposits.id\n" +
            "LEFT JOIN accounts\n" +
            "ON deposits.account_id = accounts.id\n" +
            "LEFT JOIN cards\n" +
            "ON deposits.card_id = cards.id\n" +
            "LEFT JOIN transferences\n" +
            "ON transactions.id = transferences.id\n" +
            "WHERE transferences.origin_account_id = ?1\n" +
            "GROUP BY transferences.destination_account_id\n" +
            "ORDER BY date DESC\n" +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findLastFiveAccountTransferenceByAccountId(Integer accountId);
}
