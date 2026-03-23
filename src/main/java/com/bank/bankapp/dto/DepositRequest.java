package com.bank.bankapp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DepositRequest {
    private BigDecimal amount;
}
