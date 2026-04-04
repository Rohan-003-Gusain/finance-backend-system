package com.finance.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.finance.dto.response.CategoryItem;
import com.finance.dto.response.MonthlyExpenseResponse;
import com.finance.dto.response.UserCategoryResponse;
import com.finance.dto.response.UserMonthlyDashboardResponse;
import com.finance.dto.response.UserSummaryResponse;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.TransactionEntity;
import com.finance.model.UserEntity;
import com.finance.model.enums.RecordType;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import com.finance.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository repository;
    private final UserRepository userRepository;

    private UserEntity getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<UserSummaryResponse> getSummary() {

        UserEntity loggedUser = getLoggedUser();

        List<TransactionEntity> list = repository.findByDeletedFalseWithUser(Pageable.unpaged()).getContent();

        if (!loggedUser.getRole().name().equals("ADMIN")) {
            list = list.stream()
                    .filter(t -> t.getUser().getId().equals(loggedUser.getId()))
                    .toList();
        }

        Map<UserEntity, List<TransactionEntity>> grouped =
                list.stream().collect(Collectors.groupingBy(TransactionEntity::getUser));

        return grouped.entrySet().stream()
                .map(entry -> {

                    UserEntity user = entry.getKey();

                    double income = entry.getValue().stream()
                            .filter(t -> t.getType() == RecordType.INCOME)
                            .mapToDouble(TransactionEntity::getAmount)
                            .sum();

                    double expense = entry.getValue().stream()
                            .filter(t -> t.getType() == RecordType.EXPENSE)
                            .mapToDouble(TransactionEntity::getAmount)
                            .sum();

                    return UserSummaryResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .totalIncome(income)
                            .totalExpense(expense)
                            .netBalance(income - expense)
                            .build();
                })
                .toList();
    }

    @Override
    public List<UserCategoryResponse> getCategoryWise() {

        UserEntity loggedUser = getLoggedUser();

        List<TransactionEntity> list =
                repository.findByDeletedFalseWithUser(Pageable.unpaged()).getContent();

        if (!loggedUser.getRole().name().equals("ADMIN")) {
            list = list.stream()
                    .filter(t -> t.getUser().getId().equals(loggedUser.getId()))
                    .toList();
        }

        list = list.stream()
                .filter(t -> t.getType() == RecordType.EXPENSE)
                .toList();

        Map<UserEntity, List<TransactionEntity>> userGrouped =
                list.stream().collect(Collectors.groupingBy(TransactionEntity::getUser));

        return userGrouped.entrySet().stream()
                .map(entry -> {

                    UserEntity user = entry.getKey();
                    List<TransactionEntity> userTx = entry.getValue();

                    double totalExpense = userTx.stream()
                            .mapToDouble(TransactionEntity::getAmount)
                            .sum();

                    Map<String, Double> categoryMap =
                            userTx.stream()
                                    .collect(Collectors.groupingBy(
                                            TransactionEntity::getCategory,
                                            Collectors.summingDouble(TransactionEntity::getAmount)
                                    ));

                    List<CategoryItem> categories =
                            categoryMap.entrySet().stream()
                                    .map(e -> CategoryItem.builder()
                                            .category(e.getKey())
                                            .expense(e.getValue())
                                            .build())
                                    .collect(Collectors.toList());

                    return UserCategoryResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .totalExpense(totalExpense)
                            .categories(categories)
                            .build();
                })
                .toList();
    }

    @Override
    public List<UserMonthlyDashboardResponse> getMonthlyDashboard() {

        UserEntity loggedUser = getLoggedUser();

        List<TransactionEntity> list =
                repository.findByDeletedFalseWithUser(Pageable.unpaged()).getContent();

        if (!loggedUser.getRole().name().equals("ADMIN")) {
            list = list.stream()
                    .filter(t -> t.getUser().getId().equals(loggedUser.getId()))
                    .toList();
        }

        Map<UserEntity, List<TransactionEntity>> userGrouped =
                list.stream().collect(Collectors.groupingBy(TransactionEntity::getUser));

        return userGrouped.entrySet().stream()
                .map((Map.Entry<UserEntity, List<TransactionEntity>> entry) -> {

                    UserEntity user = entry.getKey();
                    List<TransactionEntity> userTransactions = entry.getValue();

                    Map<String, List<TransactionEntity>> monthGrouped =
                            userTransactions.stream()
                                    .collect(Collectors.groupingBy(
                                            t -> t.getDate().getMonth().name().substring(0, 3)
                                    ));

                    List<MonthlyExpenseResponse> monthlyExpenses =
                            monthGrouped.entrySet().stream()
                                    .map(e -> {
                                        double monthlyIncome = e.getValue().stream()
                                                .filter(t -> t.getType() == RecordType.INCOME)
                                                .mapToDouble(TransactionEntity::getAmount)
                                                .sum();

                                        double expense = e.getValue().stream()
                                                .filter(t -> t.getType() == RecordType.EXPENSE)
                                                .mapToDouble(TransactionEntity::getAmount)
                                                .sum();

                                        return MonthlyExpenseResponse.builder()
                                                .month(e.getKey())
                                                .income(monthlyIncome)
                                                .totalExpense(expense) 
                                                .build();
                                    })
                                    .collect(Collectors.toList());

                    return UserMonthlyDashboardResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .monthlyExpenses(monthlyExpenses)
                            .build();
                })
                .collect(Collectors.toList());
    }
}