package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.dto.CardRequest;
import com.digitalmoneyhouse.accountservice.dto.ReceiptContainer;
import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import com.digitalmoneyhouse.accountservice.dto.TransactionResponse;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.model.Card;
import com.digitalmoneyhouse.accountservice.model.Transaction;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.digitalmoneyhouse.accountservice.service.CardService;
import com.digitalmoneyhouse.accountservice.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteByCardAndIdAccountId(@PathVariable Integer cardId, @PathVariable Integer accountId) throws BusinessException {
        cardService.deleteByIdAndAccountId(cardId, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<Transaction> saveTransaction(@PathVariable Integer accountId, @Valid @RequestBody TransactionRequest transactionRequest) throws BusinessException {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionRequest, accountId));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<Page<TransactionResponse>> findTransactions(
            @PathVariable Integer accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String transactionCategory,
            @RequestParam(required = false) Double minimumAmount,
            @RequestParam(required = false) Double maximumAmount,
            Pageable pageable
    ) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.find(accountId, type, startDate, endDate, transactionCategory, minimumAmount, maximumAmount, pageable));
    }

    @GetMapping("/{accountId}/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> findTransactionById(@PathVariable Integer accountId, @PathVariable Integer transactionId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findByIdAndAccountId(transactionId, accountId));
    }

    @GetMapping("/{accountId}/transactions/{transactionId}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Integer accountId, @PathVariable Integer transactionId) throws IOException, BusinessException {
        ReceiptContainer receiptContainer = transactionService.getTransactionReceipt(transactionId, accountId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.pdf", receiptContainer.getTransactionCode()));
       return ResponseEntity.ok().headers(headers).body(receiptContainer.getBytes());
    }

    @GetMapping("/{accountId}/transactions/recent-transference")
    public ResponseEntity<List<TransactionResponse>> findLastFiveAccountTransferenceByAccountId(
            @PathVariable Integer accountId
    ) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findLastFiveAccountTransferenceByAccountId(accountId));
    }
}
