package com.finance.service;

import java.util.List;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.request.UpdateTransactionRequest;
import com.finance.dto.response.PageResponse;
import com.finance.dto.response.TransactionResponse;
import com.finance.dto.response.UserExpenseResponse;
import com.finance.model.enums.RecordType;

public interface TransactionService {

    TransactionResponse create(CreateTransactionRequest request);

    PageResponse<UserExpenseResponse> getAll(int page, int size);

    TransactionResponse getById(Long id);

    TransactionResponse update(Long id, UpdateTransactionRequest request);

    void delete(Long id);

    List<?> getByType(RecordType type);
    
	List<TransactionResponse> getByCategory(String category);
}