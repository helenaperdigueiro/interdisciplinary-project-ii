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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentsGenerator documentsGenerator;

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
        Card card = cardRepository.findByIdAndAccountIdAndDeletedFalse(transactionRequest.getCardId(), loggedAccountId).orElseThrow(CardNotFoundException::new);
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

    public Page<TransactionResponse> find(Integer accountId, String transactionType, String startDate, String endDate, String transactionCategory, Double minimumAmount, Double maximumAmount, Pageable pageable) throws BusinessException {
        pageable = validatePageable(pageable);
        validateParams(transactionType, transactionCategory, startDate, endDate);
        accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<TransactionResponse> transactions = new ArrayList<>();
        List<Object[]> results = transactionRepository.findAllByAccountId(accountId, transactionType, startDate, endDate, transactionCategory, minimumAmount, maximumAmount, pageable);
        for (Object[] result : results) {
            transactions.add(resolveTransactionResponse(result));
        }
        return PageableExecutionUtils.getPage(transactions, pageable, transactions::size);
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

    public DocumentContainer getTransactionReceipt(Integer transactionId, Integer accountId) throws IOException,  BusinessException {
        TransactionResponse transactionResponse = findByIdAndAccountId(transactionId, accountId);
        return documentsGenerator.generateReceipt(transactionResponse);
    }

    public DocumentContainer getMonthlyReport(Integer accountId, String referenceMonth, String contentType) throws BusinessException, IOException {
        String pattern = "^\\d{4}-\\d{2}$";
        if (!Pattern.matches(pattern, referenceMonth))
            throw new BusinessException(400, "Param 'referenceMonth' must be in format YYYY-MM");

        YearMonth yearMonth = YearMonth.parse(referenceMonth);
        if (!yearMonth.isBefore(YearMonth.now())) {
            throw new BusinessException(400, "Value for param 'referenceMonth' must be before the current month");
        }
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        String startDate =yearMonth.atDay(1).toString();
        String endDate = yearMonth.atEndOfMonth().toString();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("date").ascending());
        List<TransactionResponse> transactions = new ArrayList<>();
        List<Object[]> results = transactionRepository.findAllByAccountId(accountId, null, startDate, endDate, null, null, null, pageable);
        for (Object[] result : results) {
            transactions.add(resolveTransactionResponse(result));
        }

        return documentsGenerator.generateReport(account, yearMonth, transactions, contentType);
    }

    public List<TransactionResponse> findLastFiveAccountTransferenceByAccountId(Integer accountId) throws BusinessException {
        accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        List<TransactionResponse> transactions = new ArrayList<>();
        List<Object[]> results = transactionRepository.findLastFiveAccountTransferenceByAccountId(accountId);
        for (Object[] result : results) {
            transactions.add(resolveTransactionResponse(result));
        }
        return transactions;
    }

    private Pageable validatePageable(Pageable pageable) throws BusinessException {
        Integer pageSize = pageable.getPageSize();
        Integer pageNumber = pageable.getPageNumber();
        Sort sortList = pageable.getSort();
        for (Sort.Order order : sortList) {
            String property = order.getProperty();
            List<String> acceptedSortValues = Arrays.asList("id", "amount", "date", "type", "transaction_code", "description");
            if (!acceptedSortValues.contains(property)) {
                throw new BusinessException(400, "Sort parameter is not valid.");
            }
        }
        if(sortList.isUnsorted()) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by("date").descending());
        }
        return pageable;
    }

    public void validateTransactionType(String transactionType) throws BusinessException {
        if (transactionType != null) {
            try {
                TransactionType.valueOf(transactionType.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new InvalidTransactionTypeException();
            }
        }
    }

    public void validateTransactionCategory(String transactionCategory) throws BusinessException {
        if (transactionCategory != null) {
            try {
                TransactionCategory.valueOf(transactionCategory.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new InvalidTransactionCategoryException();
            }
        }
    }

    public void validateDates(String startDate, String endDate) throws BusinessException {
        String pattern = "^\\d{4}-\\d{2}-\\d{2}$";

        if (startDate != null) {
            try {
                if (!Pattern.matches(pattern, startDate)) {
                    throw new BusinessException(400, "Param 'startDate' must be in format YYYY-MM-DD");
                }
                LocalDate.parse(startDate);
            } catch (DateTimeParseException ex) {
                throw new BusinessException(400, String.format("Value '%s' for param 'startDate' is not a valid date", startDate));
            }
        }

        if (startDate != null) {
            try {
                if (!Pattern.matches(pattern, endDate)) {
                    throw new BusinessException(400, "Param 'endDate' must be in format YYYY-MM-DD");
                }
                LocalDate.parse(endDate);
            } catch (DateTimeParseException ex) {
                throw new BusinessException(400, String.format("Value '%s' for param 'endDate' is not a valid date", endDate));
            }
        }
    }

    private void validateParams(String transactionType, String transactionCategory, String startDate, String endDate) throws BusinessException {
        validateTransactionType(transactionType);
        validateTransactionCategory(transactionCategory);
        validateDates(startDate, endDate);
    }

}
