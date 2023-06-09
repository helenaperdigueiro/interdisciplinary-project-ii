package com.digitalmoneyhouse.accountservice.controller;

import com.digitalmoneyhouse.accountservice.dto.*;
import com.digitalmoneyhouse.accountservice.exception.BusinessException;
import com.digitalmoneyhouse.accountservice.model.Account;
import com.digitalmoneyhouse.accountservice.service.AccountService;
import com.digitalmoneyhouse.accountservice.service.CardService;
import com.digitalmoneyhouse.accountservice.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.*;
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
    public ResponseEntity<CardResponse> saveCard(@RequestBody CardRequest card, @PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.save(card, accountId));
    }

    @GetMapping("/{accountId}/cards")
    public ResponseEntity<List<CardResponse>> findByAccountId(@PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.findByAccountId(accountId));
    }

    @GetMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<CardResponse> findByCardIdAndAccountId(@PathVariable Integer cardId, @PathVariable Integer accountId) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.findByIdAndAccountId(cardId, accountId));
    }

    @DeleteMapping("/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> deleteByCardAndIdAccountId(@PathVariable Integer cardId, @PathVariable Integer accountId) throws BusinessException {
        cardService.deleteByIdAndAccountId(cardId, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> saveTransaction(@PathVariable Integer accountId, @Valid @RequestBody TransactionRequest transactionRequest) throws BusinessException {
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
        DocumentContainer documentContainer = transactionService.getTransactionReceipt(transactionId, accountId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", documentContainer.getFileName()));
       return ResponseEntity.ok().headers(headers).body(documentContainer.getBytes());
    }

    @GetMapping("/{accountId}/transactions/recent-transference")
    public ResponseEntity<List<TransactionResponse>> findLastFiveAccountTransferenceByAccountId(
            @PathVariable Integer accountId
    ) throws BusinessException {
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.findLastFiveAccountTransferenceByAccountId(accountId));
    }

    @GetMapping("/{accountId}/transactions/reports")
    public ResponseEntity<byte[]> getMonthlyReport(@PathVariable Integer accountId, @RequestParam String referenceMonth, HttpServletRequest request) throws IOException, BusinessException, FontFormatException {
        String contentType = request.getHeader("Content-Type");
        DocumentContainer documentContainer = transactionService.getMonthlyReport(accountId, referenceMonth, contentType);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", documentContainer.getFileName()));
        if (contentType == null || contentType.equals("text/csv")) {
            headers.setContentType(MediaType.TEXT_PLAIN);
        } else if (contentType.equals("application/pdf")) {
            headers.setContentType(MediaType.APPLICATION_PDF);
        }
        return ResponseEntity.ok().headers(headers).body(documentContainer.getBytes());
    }
}
