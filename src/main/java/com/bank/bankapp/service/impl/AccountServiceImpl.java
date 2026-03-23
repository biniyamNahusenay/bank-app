package com.bank.bankapp.service.impl;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bank.bankapp.dto.BankAccountDto;
import com.bank.bankapp.entity.BankAccount;
import com.bank.bankapp.entity.User;
import com.bank.bankapp.repository.BankAccountRepository;
import com.bank.bankapp.repository.UserRepository;
import com.bank.bankapp.service.AccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final BankAccountRepository accountRepository;
    private final UserRepository userRepository;

    // 🔹 1. Create Account
    @Override
    public BankAccountDto createAccount(String accountType) {

        User user = getCurrentUser();

        // 🔥 Prevent multiple accounts (optional rule)
        if (accountRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("User already has an account");
        }

        // 🔢 Generate unique account number
        String accountNumber = generateAccountNumber();

        // 💾 Create account
        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(accountType);
        account.setUser(user);

        accountRepository.save(account);

        return mapToDto(account);
    }

    // 🔹 2. Get Logged-in User Account (Dashboard)
    @Override
    public BankAccountDto getMyAccount() {
        User user = getCurrentUser();

        BankAccount account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return mapToDto(account);
    }

    // 🔹 3. Get Account by Number (SECURITY CHECK)
    @Override
    public BankAccountDto getAccountByNumber(String accountNumber) {

        BankAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 🔐 SECURITY: Ensure owner only
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (!account.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access!");
        }

        return mapToDto(account);
    }

    @Override
    public BankAccountDto depositToMyAccount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        User user = getCurrentUser();
        BankAccount account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        return mapToDto(account);
    }

    // 🔧 Helper: Generate Account Number
    private String generateAccountNumber() {
        Random random = new Random();
        return "ACC" + (100000 + random.nextInt(900000));
    }

    // 🔧 Helper: Map Entity → DTO
    private BankAccountDto mapToDto(BankAccount account) {
        BankAccountDto dto = new BankAccountDto();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setAccountType(account.getAccountType());
        return dto;
    }

    private User getCurrentUser() {
        String usernameOrEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
