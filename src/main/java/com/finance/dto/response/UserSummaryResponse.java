package com.finance.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummaryResponse {

    private Long userId;
    private String name;
    private double totalIncome;
    private double totalExpense;
    private double netBalance;
}