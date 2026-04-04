package com.finance.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCategoryResponse {

    private Long userId;
    private String name;
    private double totalExpense;
    private List<CategoryItem> categories;
}