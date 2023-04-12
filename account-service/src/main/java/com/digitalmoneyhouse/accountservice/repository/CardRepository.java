package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card,Integer> {
    boolean existsByNumber(String number);

    boolean existsByNumberAndDeletedFalse(String number);

    List<Card> findByAccountIdAndDeletedFalse(Integer accountId);
    Optional<Card> findByIdAndAccountIdAndDeletedFalse(Integer cardId, Integer accountId);
    void deleteByIdAndAccountId(Integer cardId, Integer accountId);

    @Transactional
    @Modifying
    @Query("UPDATE Card c SET c.deleted = true WHERE c.id = :cardId")
    Integer delete(Integer cardId);
}
