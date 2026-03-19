package com.bank.bankapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber; // Generate a unique 10-12 digit string

    private BigDecimal balance = BigDecimal.ZERO;

    private String accountType; // SAVINGS, CHECKING

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}