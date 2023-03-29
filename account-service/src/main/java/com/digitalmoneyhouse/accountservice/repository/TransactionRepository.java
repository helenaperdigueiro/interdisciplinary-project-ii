package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = "SELECT\n" +
            "ts.id,\n" +
            "ts.amount,\n" +
            "ts.date,\n" +
            "ts.type,\n" +
            "ts.transaction_code,\n" +
            "ts.description,\n" +
            "d.card_id,\n" +
            "c.number,\n" +
            "d.account_id,\n" +
            "a.account_number,\n" +
            "t.origin_account_id,\n" +
            "t.destination_account_id\n" +
            "FROM transactions ts\n" +
            "LEFT JOIN deposits d\n" +
            "ON ts.id = d.id\n" +
            "LEFT JOIN accounts a\n" +
            "ON d.account_id = a.id\n" +
            "LEFT JOIN cards c\n" +
            "ON d.card_id = c.id\n" +
            "LEFT JOIN transferences t\n" +
            "ON ts.id = t.id\n" +
            "WHERE (\n" +
            "CASE\n" +
            "WHEN :transactionCategory = 'REVENUE' THEN (d.account_id = :accountId OR t.destination_account_id = :accountId)\n" +
            "WHEN :transactionCategory = 'EXPENSE' THEN (t.origin_account_id = :accountId)\n" +
            "ELSE (d.account_id = :accountId OR t.origin_account_id = :accountId OR t.destination_account_id = :accountId)\n" +
            "END)\n" +
            "AND (:type IS NULL OR type = :type)\n" +
            "AND (:minimumAmount IS NULL OR amount >= :minimumAmount) AND (:maximumAmount IS NULL OR amount <= :maximumAmount)\n" +
            "AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate)", nativeQuery = true)
    List<Object[]> findAllByAccountId(
            Integer accountId,
            String type,
            String startDate, String endDate,
            String transactionCategory,
            Double minimumAmount, Double maximumAmount,
            Pageable pageable
    );

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
