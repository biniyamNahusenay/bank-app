package com.bank.bankapp.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.bank.bankapp.dto.MiniStatementResponseDto;
import com.bank.bankapp.dto.TransactionHistoryItemDto;
import com.bank.bankapp.dto.TransferRequest;
import com.bank.bankapp.entity.BankAccount;
import com.bank.bankapp.entity.Transaction;
import com.bank.bankapp.repository.BankAccountRepository;
import com.bank.bankapp.repository.TransactionRepository;
import com.bank.bankapp.service.TransactionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final BankAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional // CRITICAL: Ensures Atomicity
    @Override
    public String transferMoney(String username, TransferRequest request) {
        // 1. Find and Validate Source Account
        BankAccount fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        // 2. Security Check: Does this account belong to the logged-in user?
        if (!fromAccount.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You do not own this account");
        }

        // 3. Find Destination Account
        BankAccount toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // 4. Balance Check
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // 5. Perform the Transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 6. Log the Transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setSourceAccountNumber(fromAccount.getAccountNumber());
        transaction.setDestinationAccountNumber(toAccount.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        transactionRepository.save(transaction);

        return "Transfer successful! Transaction ID: " + transaction.getTransactionId();
    }

    @Override
    public MiniStatementResponseDto getTransactionHistory(
            String accountNumber,
            String username,
            String filter,
            int page,
            int size) {
        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You do not own this account");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        String normalizedFilter = normalizeFilter(filter);

        Page<Transaction> transactionPage = switch (normalizedFilter) {
            case "DEBITED" -> transactionRepository.findDebitedTransactions(accountNumber, PageRequest.of(safePage, safeSize));
            case "CREDITED" -> transactionRepository.findCreditedTransactions(accountNumber, PageRequest.of(safePage, safeSize));
            default -> transactionRepository.findByAccountNumber(accountNumber, PageRequest.of(safePage, safeSize));
        };

        MiniStatementResponseDto response = new MiniStatementResponseDto();
        response.setTransactions(transactionPage.getContent().stream()
                .map(transaction -> mapToHistoryItem(transaction, accountNumber))
                .collect(Collectors.toList()));
        response.setCurrentPage(transactionPage.getNumber());
        response.setPageSize(transactionPage.getSize());
        response.setTotalPages(transactionPage.getTotalPages());
        response.setTotalTransactions(transactionPage.getTotalElements());
        response.setHasNext(transactionPage.hasNext());
        response.setHasPrevious(transactionPage.hasPrevious());
        return response;
    }

    private TransactionHistoryItemDto mapToHistoryItem(Transaction transaction, String accountNumber) {
        TransactionHistoryItemDto dto = new TransactionHistoryItemDto();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setSourceAccountNumber(transaction.getSourceAccountNumber());
        dto.setDestinationAccountNumber(transaction.getDestinationAccountNumber());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setStatus(transaction.getStatus());
        dto.setTransactionType(resolveTransactionType(transaction, accountNumber));
        return dto;
    }

    private String resolveTransactionType(Transaction transaction, String accountNumber) {
        if (accountNumber.equals(transaction.getSourceAccountNumber())) {
            return "DEBIT";
        }

        if (accountNumber.equals(transaction.getDestinationAccountNumber())) {
            return "CREDIT";
        }

        return "UNKNOWN";
    }

    private String normalizeFilter(String filter) {
        if (filter == null || filter.isBlank()) {
            return "ALL";
        }

        String normalized = filter.trim().toUpperCase();
        return switch (normalized) {
            case "ALL", "DEBITED", "CREDITED" -> normalized;
            default -> throw new RuntimeException("Invalid filter. Use all, debited, or credited");
        };
    }
}
