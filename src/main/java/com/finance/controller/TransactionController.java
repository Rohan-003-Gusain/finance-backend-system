package com.finance.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.request.UpdateTransactionRequest;
import com.finance.dto.response.PageResponse;
import com.finance.dto.response.TransactionResponse;
import com.finance.dto.response.UserExpenseResponse;
import com.finance.model.enums.RecordType;
import com.finance.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @Operation(
    	    summary = "Create Transaction",
    	    description = """
    	    ADMIN:
    	    - Can create income & expense
    	    - Can assign transaction to any user using userId

    	    ANALYST:
    	    - Can create only expense
    	    - userId will be ignored
    	    """
    	)
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @PostMapping
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        return service.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping
    public PageResponse<UserExpenseResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return service.getAll(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/filter/type")
    public List<?> getByType(@RequestParam RecordType type) {
        return service.getByType(type);
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @GetMapping("/filter/category")
    public List<TransactionResponse> getByCategory(@RequestParam String category) {
        return service.getByCategory(category);
    }

    @Operation(
    	    summary = "Update Transaction",
    	    description = """
    	    ADMIN:
    	    - Can update any transaction

    	    ANALYST:
    	    - Can update only own expense
    	    - Cannot update income
    	    """
    	)
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @PutMapping("/{id}")
    public TransactionResponse update(@Valid @PathVariable Long id,
                                      @RequestBody UpdateTransactionRequest request) {
        return service.update(id, request);
    }

    @Operation(
    	    summary = "Delete Transaction",
    	    description = """
    	    ADMIN:
    	    - Can delete any transaction

    	    ANALYST:
    	    - Can delete only own expense
    	    """
    	)
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
}