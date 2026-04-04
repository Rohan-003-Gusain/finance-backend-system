package com.finance.service;

import java.util.List;

import com.finance.dto.response.UserCategoryResponse;
import com.finance.dto.response.UserMonthlyDashboardResponse;
import com.finance.dto.response.UserSummaryResponse;

public interface DashboardService {

    List<UserSummaryResponse> getSummary();

    List<UserCategoryResponse> getCategoryWise();

    List<UserMonthlyDashboardResponse> getMonthlyDashboard();
}