package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.CardRequest;
import com.digitalmoneyhouse.accountservice.exception.AccountNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.exception.DuplicatedCardNumberException;
import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Card save(CardRequest cardRequest, Integer accountId) throws BusinessException {
        if(cardRepository.existsByNumber(cardRequest.getNumber())) {
            throw new DuplicatedCardNumberException();
        }
        Card card = new Card(cardRequest);
        card.setAccount(accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new));
        return cardRepository.save(card);
    }

}
