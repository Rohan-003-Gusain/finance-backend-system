package com.finance.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMonthlyDashboardResponse {

    private Long userId;
    private String name;
    private List<MonthlyExpenseResponse> monthlyExpenses;
}