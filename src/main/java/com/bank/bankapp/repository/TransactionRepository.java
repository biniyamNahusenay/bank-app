package com.bank.bankapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.bankapp.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Custom query to find all transactions related to a specific account number
    // This allows the user to see both money coming in and money going out
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountNumber = :accountNo OR t.destinationAccountNumber = :accountNo ORDER BY t.timestamp DESC")
    Page<Transaction> findByAccountNumber(@Param("accountNo") String accountNo, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountNumber = :accountNo ORDER BY t.timestamp DESC")
    Page<Transaction> findDebitedTransactions(@Param("accountNo") String accountNo, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.destinationAccountNumber = :accountNo ORDER BY t.timestamp DESC")
    Page<Transaction> findCreditedTransactions(@Param("accountNo") String accountNo, Pageable pageable);
}
