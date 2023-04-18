package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.exception.AccountNotFoundException;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account save(Account account) {
        String accountNumber = generateAccountNumber();
        while(accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber();
        }

        account.setAccountNumber(accountNumber);
        account.setWalletBalance(BigDecimal.ZERO);

        return accountRepository.save(account);
    }

    public String generateAccountNumber() {
        Random generator = new Random();
        return String.valueOf(generator.nextInt(100000,1000000));
    }

    public Account findById(Integer id) throws BusinessException {
        Account account = accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
        return account;
    }
}
