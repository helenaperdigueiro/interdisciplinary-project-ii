package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.client.IamClient;
import com.digitalmoneyhouse.accountservice.dto.CardRequest;
import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.exception.InvalidTokenException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.model.Transaction;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.digitalmoneyhouse.accountservice.service.CardService;
import com.digitalmoneyhouse.accountservice.service.TransactionService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    @Value("${iamService.baseUrl}")
    private String BASE_URL;

    @Autowired
    private Gson gson;

    @Autowired
    private IamClient iamClient;

    @PostMapping()
    public ResponseEntity<Account> save(@RequestBody Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.save(account));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> findById(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer accountId) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response = iamClient.verifyToken(bearerToken);

        if (response.statusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(accountService.findById(accountId));
        } else {
            throw new InvalidTokenException();
        }
    }

    @PostMapping("/{accountId}/cards")
    public ResponseEntity<Card> saveCard(@RequestHeader("Authorization") String bearerToken, @RequestBody CardRequest card, @PathVariable Integer accountId) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response = iamClient.verifyToken(bearerToken);

        if (response.statusCode() == 200) {
            return ResponseEntity.status(HttpStatus.CREATED).body(cardService.save(card, accountId));
        } else {
            throw new InvalidTokenException();
        }
    }

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<List<Card>> findByAccountId(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer accountId) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response = iamClient.verifyToken(bearerToken);

        if (response.statusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(cardService.findByAccountId(accountId));
        } else {
            throw new InvalidTokenException();
        }

    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Card> findByCardIdAndAccountId(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer cardId, @PathVariable Integer accountId) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response = iamClient.verifyToken(bearerToken);

        if (response.statusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(cardService.findByIdAndAccountId(cardId, accountId));
        } else {
            throw new InvalidTokenException();
        }
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteByCardAndIdAccountId(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer cardId, @PathVariable Integer accountId) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response = iamClient.verifyToken(bearerToken);

        if (response.statusCode() == 200) {
            cardService.deleteByIdAndAccountId(cardId, accountId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else {
            throw new InvalidTokenException();
        }
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<Transaction> saveTransaction(@RequestHeader("Authorization") String bearerToken, @PathVariable Integer accountId, @RequestBody TransactionRequest transactionRequest) throws BusinessException, URISyntaxException, IOException, InterruptedException {
        HttpResponse response = iamClient.verifyToken(bearerToken);

        if (response.statusCode() == 200) {
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionRequest, accountId));
        } else {
            throw new InvalidTokenException();
        }
    }
}
