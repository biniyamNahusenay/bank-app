package com.bank.bankapp.service;

import java.math.BigDecimal;

import com.bank.bankapp.dto.BankAccountDto;
    
public interface AccountService {

    BankAccountDto createAccount(String accountType);

    BankAccountDto getMyAccount();

    BankAccountDto getAccountByNumber(String accountNumber);

    BankAccountDto depositToMyAccount(BigDecimal amount);
}
