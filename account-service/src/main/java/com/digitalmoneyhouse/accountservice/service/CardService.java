package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.CardRequest;
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

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Card save(CardRequest cardRequest, Integer accountId) throws BusinessException {
        if(cardRepository.existsByNumberAndDeletedFalse(cardRequest.getNumber())) {
            throw new DuplicatedCardNumberException();
        }
        Card card = new Card(cardRequest);
        card.setAccount(accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new));
        return cardRepository.save(card);
    }

    public List<Card> findByAccountId(Integer accountId) throws BusinessException {
        accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        return cardRepository.findByAccountIdAndDeletedFalse(accountId);
    }

    public Card findByIdAndAccountId(Integer cardId, Integer accountId) throws BusinessException {
        return cardRepository.findByIdAndAccountIdAndDeletedFalse(cardId, accountId).orElseThrow(CardNotFoundException::new);
    }

    @Transactional
    public void deleteByIdAndAccountId(Integer cardId, Integer accountId) throws BusinessException {
        cardRepository.findByIdAndAccountIdAndDeletedFalse(cardId, accountId).orElseThrow(CardNotFoundException::new);
        cardRepository.delete(cardId);
    }

}
