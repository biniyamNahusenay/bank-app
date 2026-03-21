package com.bank.bankapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionHistoryItemDto {

    private String transactionId;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String status;
    private String transactionType;
}
