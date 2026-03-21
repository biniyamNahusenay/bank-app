package com.bank.bankapp.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.bankapp.dto.MiniStatementResponseDto;
import com.bank.bankapp.dto.TransferRequest;
import com.bank.bankapp.service.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(Principal principal, @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transferMoney(principal.getName(), request));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<MiniStatementResponseDto> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(
                transactionService.getTransactionHistory(accountNumber, authentication.getName(), filter, page, size));
    }
 }
