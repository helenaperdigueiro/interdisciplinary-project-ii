package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card,Integer> {
    boolean existsByNumber(String number);
    List<Card> findByAccountId(Integer accountId);
    Optional<Card> findByIdAndAccountId(Integer cardId, Integer accountId);
    void deleteByIdAndAccountId(Integer cardId, Integer accountId);
}
