package com.digitalmoneyhouse.accountservice.service;

import com.digitalmoneyhouse.accountservice.dto.DepositResponse;
import com.digitalmoneyhouse.accountservice.dto.TransactionRequest;
import com.digitalmoneyhouse.accountservice.dto.TransactionResponse;
import com.digitalmoneyhouse.accountservice.dto.TransferenceResponse;
import com.digitalmoneyhouse.accountservice.exception.*;
import com.digitalmoneyhouse.accountservice.model.*;
import com.digitalmoneyhouse.accountservice.repository.AccountRepository;
import com.digitalmoneyhouse.accountservice.repository.CardRepository;
import com.digitalmoneyhouse.accountservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
            String type = (String) result[4];
            if (type.equals(TransactionType.CASH_DEPOSIT.toString())) {
                transactions.add(resultObjectToDepositResponse(result));
            } else if (type.equals(TransactionType.CASH_TRANSFERENCE.toString())) {
                transactions.add(resultObjectToTransferResponse(result));
            }
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
        deposit.setDescription((String) result[3]);
        deposit.setType((String) result[4]);
        deposit.setCardId((Integer) result[5]);
        deposit.setAccountId((Integer) result[6]);
        return deposit;
    }

    public TransferenceResponse resultObjectToTransferResponse(Object[] result) throws BusinessException {
        System.out.println(accountRepository.findAccountNumberById((Integer) result[7]));
        String originAccountNumber = accountRepository.findAccountNumberById((Integer) result[7]);
        String destinationAccountNumber = accountRepository.findAccountNumberById((Integer) result[8]);
        TransferenceResponse transference = new TransferenceResponse();
        transference.setId((Integer) result[0]);
        transference.setAmount((Double) result[1]);
        transference.setDate(((Timestamp) result[2]).toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        transference.setDescription((String) result[3]);
        transference.setType((String) result[4]);
        transference.setOriginAccount(originAccountNumber);
        transference.setDestinationAccount(destinationAccountNumber);
        return transference;
    }

}
