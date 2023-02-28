package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        account.setWalletBalance(0D);

        return accountRepository.save(account);
    }

    public String generateAccountNumber() {
        Random generator = new Random();
        return String.valueOf(generator.nextInt(100000,1000000));
    }
}
