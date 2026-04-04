package com.finance.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.finance.dto.response.UserCategoryResponse;
import com.finance.dto.response.UserMonthlyDashboardResponse;
import com.finance.dto.response.UserSummaryResponse;
import com.finance.service.DashboardService;

import lombok.RequiredArgsConstructor;

@PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/summary")
    public List<UserSummaryResponse> getSummary() {
        return service.getSummary();
    }

    @GetMapping("/category-wise")
    public List<UserCategoryResponse> getCategoryWise() {
        return service.getCategoryWise();
    }

    @GetMapping("/monthly-dashboard")
    public List<UserMonthlyDashboardResponse> getMonthlyDashboard() {
        return service.getMonthlyDashboard();
    }
}