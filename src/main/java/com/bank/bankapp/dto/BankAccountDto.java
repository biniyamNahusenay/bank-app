package com.bank.bankapp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BankAccountDto {

    private String accountNumber;
    private BigDecimal balance;

    // getters and setters
}