package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.exception.CardNotFoundException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.model.Transaction;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.repository.CardRepository;
import com.digitalmoneyhouse.accountservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public Transaction save(TransactionRequest transactionRequest, Integer accountId) throws BusinessException {
        Card card = cardRepository.findByIdAndAccountId(transactionRequest.getCardId(), accountId).orElseThrow(CardNotFoundException::new);
        Account account = card.getAccount();
        account.setWalletBalance(account.getWalletBalance() + transactionRequest.getAmount());
        accountRepository.save(account);
        Transaction transaction = new Transaction(transactionRequest, accountId);
        return transactionRepository.save(transaction);
    }
}
