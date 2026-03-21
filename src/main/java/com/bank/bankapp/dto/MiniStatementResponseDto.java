package com.bank.bankapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class MiniStatementResponseDto {

    private List<TransactionHistoryItemDto> transactions;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalTransactions;
    private boolean hasNext;
    private boolean hasPrevious;
}
