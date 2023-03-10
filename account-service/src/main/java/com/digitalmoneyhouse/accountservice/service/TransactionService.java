package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import com.digitalmoneyhouse.accountservice.exception.*;
import com.digitalmoneyhouse.accountservice.model.*;
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
    public Transaction save(TransactionRequest transactionRequest, Integer loggedAccountId) throws BusinessException {
        Transaction transaction = new Transaction();
        if (transactionRequest.getType().equals(TransactionType.CASH_DEPOSIT)) {
            transaction = makeDeposit(transactionRequest, loggedAccountId);
        } else if (transactionRequest.getType().equals(TransactionType.CASH_TRANSFERENCE)) {
            transaction = makeTransference(transactionRequest, loggedAccountId);
        }
        return transactionRepository.save(transaction);
    }

    private Deposit makeDeposit(TransactionRequest transactionRequest, Integer loggedAccountId) throws BusinessException {
        Card card = cardRepository.findByIdAndAccountId(transactionRequest.getCardId(), loggedAccountId).orElseThrow(CardNotFoundException::new);
        Account account = card.getAccount();
        account.setWalletBalance(account.getWalletBalance() + transactionRequest.getAmount());
        accountRepository.save(account);
        Deposit deposit = new Deposit(transactionRequest, account);
        return transactionRepository.save(deposit);
    }

    private Transference makeTransference(TransactionRequest transactionRequest, Integer loggedAccountId) throws BusinessException {
        Double transferenceAmount = transactionRequest.getAmount();
        Account originAccount = accountRepository.findById(loggedAccountId).orElseThrow(AccountNotFoundException::new);
        Account destinationAccount = accountRepository.findByAccountNumber(transactionRequest.getDestinationAccount()).orElseThrow(AccountNotFoundException::new);

        if (transferenceAmount > originAccount.getWalletBalance()) {
            throw new InsufficientBalanceException();
        }

        originAccount.setWalletBalance(originAccount.getWalletBalance() - transferenceAmount);
        destinationAccount.setWalletBalance(destinationAccount.getWalletBalance() + transferenceAmount);

        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        Transference transference = new Transference(transactionRequest, originAccount, destinationAccount);
        return transactionRepository.save(transference);
    }

}
