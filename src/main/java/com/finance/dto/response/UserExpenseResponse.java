package com.finance.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserExpenseResponse {

    private Long userId;
    private String name;
    private Double salary;
    private List<ExpenseResponse> expenses;
}
