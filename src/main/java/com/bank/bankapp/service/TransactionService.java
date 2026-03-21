package com.bank.bankapp.service;

import com.bank.bankapp.dto.MiniStatementResponseDto;
import com.bank.bankapp.dto.TransferRequest;

public interface TransactionService {
    String transferMoney(String username, TransferRequest request);

    MiniStatementResponseDto getTransactionHistory(String accountNumber, String username, String filter, int page, int size);
}
