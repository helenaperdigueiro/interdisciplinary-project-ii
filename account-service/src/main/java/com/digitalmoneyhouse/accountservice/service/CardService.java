package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.CardRequest;
import com.digitalmoneyhouse.accountservice.dto.CardResponse;
import com.digitalmoneyhouse.accountservice.exception.AccountNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.exception.CardNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.DuplicatedCardNumberException;
import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public CardResponse save(CardRequest cardRequest, Integer accountId) throws BusinessException {
        if(cardRepository.existsByNumberAndDeletedFalse(cardRequest.getNumber())) {
            throw new DuplicatedCardNumberException();
        }
        Card card = new Card(cardRequest);
        card.setAccount(accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new));
        Card savedCard = cardRepository.save(card);
        return new CardResponse(savedCard);
    }

    public List<CardResponse> findByAccountId(Integer accountId) throws BusinessException {
        accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<Card> cards = cardRepository.findByAccountIdAndDeletedFalse(accountId);
        List<CardResponse> response = new ArrayList<>();
        for (Card card : cards) {
            response.add(new CardResponse(card));
        }
        return response;
    }

    public CardResponse findByIdAndAccountId(Integer cardId, Integer accountId) throws BusinessException {
        Card cardFound = cardRepository.findByIdAndAccountIdAndDeletedFalse(cardId, accountId).orElseThrow(CardNotFoundException::new);
        return new CardResponse(cardFound);
    }

    @Transactional
    public void deleteByIdAndAccountId(Integer cardId, Integer accountId) throws BusinessException {
        cardRepository.findByIdAndAccountIdAndDeletedFalse(cardId, accountId).orElseThrow(CardNotFoundException::new);
        cardRepository.delete(cardId);
    }

}
