package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.*;
import com.digitalmoneyhouse.accountservice.exception.*;
import com.digitalmoneyhouse.accountservice.model.*;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.repository.CardRepository;
import com.digitalmoneyhouse.accountservice.repository.TransactionRepository;
import com.digitalmoneyhouse.accountservice.util.DocumentsGenerator;
import com.digitalmoneyhouse.accountservice.util.Formatter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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

    public List<TransactionResponse> find(Integer accountId, String transactionType, Integer limit) throws BusinessException {
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        limit = (limit == null) ? 10 : limit;
        List<TransactionResponse> transactions = new ArrayList<>();
        List<Object[]> results = new ArrayList<>();
        if (transactionType != null) {
            if (isTypeValid(transactionType)) {
                results = transactionRepository.findALlByAccountIdAndType(accountId, transactionType, limit);
            }
        } else {
            results = transactionRepository.findALlByAccountId(accountId, limit);
        }
        for (Object[] result : results) {
            transactions.add(resolveTransactionResponse(result));
        }
        return transactions;
    }

    public boolean isTypeValid(String transactionType) throws BusinessException {
        try {
            TransactionType.valueOf(transactionType.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            throw new InvalidTransactionTypeException();
        }
    }

    public DepositResponse resultObjectToDepositResponse(Object[] result) {
        DepositResponse deposit = new DepositResponse();
        deposit.setId((Integer) result[0]);
        deposit.setAmount((Double) result[1]);
        deposit.setDate(((Timestamp) result[2]).toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        deposit.setType((String) result[3]);
        deposit.setTransactionCode((String) result[4]);
        deposit.setDescription((String) result[5]);
        deposit.setCardId((Integer) result[6]);
        deposit.setCardNumber(Formatter.maskCardNumber((String) result[7]));
        deposit.setAccountId((Integer) result[8]);
        deposit.setAccountNumber((String) result[9]);
        return deposit;
    }

    public TransferenceResponse resultObjectToTransferResponse(Object[] result) {
        String originAccountNumber = accountRepository.findAccountNumberById((Integer) result[10]);
        String originAccountHolderName = accountRepository.findAccountHolderNameById((Integer) result[10]);
        String destinationAccountNumber = accountRepository.findAccountNumberById((Integer) result[11]);
        String destinationAccountHolderName = accountRepository.findAccountHolderNameById((Integer) result[11]);
        TransferenceResponse transference = new TransferenceResponse();
        transference.setId((Integer) result[0]);
        transference.setAmount((Double) result[1]);
        transference.setDate(((Timestamp) result[2]).toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        transference.setType((String) result[3]);
        transference.setTransactionCode((String) result[4]);
        transference.setDescription((String) result[5]);
        transference.setOriginAccountNumber(originAccountNumber);
        transference.setOriginAccountHolderName(originAccountHolderName);
        transference.setDestinationAccountNumber(destinationAccountNumber);
        transference.setDestinationAccountHolderName(destinationAccountHolderName);
        return transference;
    }

    public TransactionResponse findByIdAndAccountId(Integer transactionId, Integer accountId) throws BusinessException {
        List<Object[]> transactionObjects = transactionRepository.findByIdAndAccountId(transactionId, accountId);
        if (transactionObjects.size() < 1) {
            throw new TransactionNotFoundException();
        }
        Object[] transactionObject = transactionObjects.get(0);
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse = resolveTransactionResponse(transactionObject);
        return transactionResponse;
    }

    public TransactionResponse resolveTransactionResponse(Object[] transactionObject) {
        TransactionResponse transactionResponse = new TransferenceResponse();
        String transactionType = (String) transactionObject[3];
        if (transactionType.equals(TransactionType.CASH_DEPOSIT.toString())) {
            transactionResponse = resultObjectToDepositResponse(transactionObject);
        } else if (transactionType.equals(TransactionType.CASH_TRANSFERENCE.toString())) {
            transactionResponse = resultObjectToTransferResponse(transactionObject);
        }
        return transactionResponse;
    }

    public ReceiptContainer getTransferenceReceipt(Integer transactionId, Integer accountId) throws IOException,  BusinessException {
        TransactionResponse transactionResponse = findByIdAndAccountId(transactionId, accountId);
        return DocumentsGenerator.generateReceipt(transactionResponse);
    }

    public List<TransactionResponse> findLastFiveAccountTransferenceByAccountId(Integer accountId) throws BusinessException {
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<TransactionResponse> transactions = new ArrayList<>();
        List<Object[]> results = transactionRepository.findLastFiveAccountTransferenceByAccountId(accountId);
        for (Object[] result : results) {
            transactions.add(resolveTransactionResponse(result));
        }
        return transactions;
    }

}
