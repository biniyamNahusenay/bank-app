package com.bank.bankapp.service;

import com.bank.bankapp.dto.BankAccountDto;
    
public interface AccountService {

    BankAccountDto createAccount();

    BankAccountDto getMyAccount();

    BankAccountDto getAccountByNumber(String accountNumber);
}