package com.digitalmoneyhouse.accountservice.repository;

import com.digitalmoneyhouse.accountservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card,Integer> {
    boolean existsByNumber(String number);
}
