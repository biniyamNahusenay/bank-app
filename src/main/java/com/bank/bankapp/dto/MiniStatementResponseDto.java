package com.bank.bankapp.dto;

import java.util.List;

import lombok.Data;

@Data
public class MiniStatementResponseDto {

    private List<TransactionHistoryItemDto> transactions;
    private int currentPage;
    private int pageSize;
    private int nextPage;
    private int previousPage;
    private int totalPages;
    private long totalTransactions;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;
}
