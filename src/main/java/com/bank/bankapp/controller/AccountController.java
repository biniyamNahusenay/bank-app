package com.bank.bankapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.bankapp.dto.BankAccountDto;
import com.bank.bankapp.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // 🔹 1. Create Account
    @PostMapping
    public ResponseEntity<BankAccountDto> createAccount(
            @RequestParam String accountType) {

        BankAccountDto account = accountService.createAccount(accountType);
        return ResponseEntity.ok(account);
    }

    // 🔹 2. Get My Account (Dashboard)
    @GetMapping("/me")
    public ResponseEntity<BankAccountDto> getMyAccount() {
        return ResponseEntity.ok(accountService.getMyAccount());
    }

    // 🔹 3. Get Account by Number (Secure)
    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountDto> getAccountByNumber(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getAccountByNumber(accountNumber)
        );
    }
}