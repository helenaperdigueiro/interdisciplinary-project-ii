package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.dto.CardRequest;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.digitalmoneyhouse.accountservice.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    @PostMapping()
    public ResponseEntity<Account> save(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(account));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> findById(@PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.findById(accountId));
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<Card> saveCard(@RequestBody CardRequest card, @PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.save(card, accountId));
    }

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<List<Card>> findByAccountId(@PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.findByAccountId(accountId));
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Card> findByCardIdAndAccountId(@PathVariable Integer cardId, @PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.findByIdAndAccountId(cardId, accountId));
    }
}
