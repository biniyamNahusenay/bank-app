package com.bank.bankapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.bankapp.entity.BankAccount;
import com.bank.bankapp.entity.User;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    Optional<BankAccount> findByUser(User user);
}