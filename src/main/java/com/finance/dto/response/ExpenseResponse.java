package com.finance.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseResponse {

    private Double amount;
    private String category;
    private LocalDate date;
    private String note;
}