package com.finance.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyExpenseResponse {

    private String month;
    private double income;
    private double totalExpense;
}