package com.bank.bankapp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId; // Unique UUID for tracking
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED
}