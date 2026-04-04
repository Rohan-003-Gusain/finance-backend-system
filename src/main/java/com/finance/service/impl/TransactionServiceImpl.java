package com.finance.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.request.UpdateTransactionRequest;
import com.finance.dto.response.ExpenseResponse;
import com.finance.dto.response.PageResponse;
import com.finance.dto.response.TransactionResponse;
import com.finance.dto.response.UserExpenseResponse;
import com.finance.exception.BadRequestException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.mapper.TransactionMapper;
import com.finance.model.TransactionEntity;
import com.finance.model.UserEntity;
import com.finance.model.enums.RecordType;
import com.finance.model.enums.Role;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import com.finance.service.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    private UserEntity getLoggedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public TransactionResponse create(CreateTransactionRequest request) {

        UserEntity loggedUser = getLoggedUser();

        if (!isAdmin(loggedUser)
                && request.getType() == RecordType.INCOME) {
            throw new BadRequestException("Only admin can add income");
        }

        validateTransaction(request.getType(), request.getCategory());

        TransactionEntity entity = mapper.toEntity(request);

        if (entity.getDate() == null) {
            entity.setDate(LocalDate.now());
        }

        UserEntity targetUser;

        if (isAdmin(loggedUser)) {

            if (request.getUserId() != null) {
                targetUser = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            } else {
                targetUser = loggedUser;
            }

        } else {
            targetUser = loggedUser;
        }

        entity.setUser(targetUser);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public PageResponse<UserExpenseResponse> getAll(int page, int size) {

        UserEntity loggedUser = getLoggedUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<TransactionEntity> pageData;

        if (loggedUser.getRole().name().equals("ADMIN")) {
            pageData = repository.findByDeletedFalseWithUser(pageable);
        } else {
            pageData = repository.findByUserAndDeletedFalseWithUser(loggedUser, pageable);
        }

        List<UserExpenseResponse> content =
                buildUserExpenseResponse(pageData.getContent());

        return PageResponse.<UserExpenseResponse>builder()
                .content(content)
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .last(pageData.isLast())
                .build();
    }

    @Override
    public List<?> getByType(RecordType type) {

        UserEntity loggedUser = getLoggedUser();

        List<TransactionEntity> list = repository.findByTypeAndDeletedFalseWithUser(type);

        if (!isAdmin(loggedUser)) {
            list = list.stream()
                    .filter(t -> t.getUser().getId().equals(loggedUser.getId()))
                    .toList();
        }

        if (type == RecordType.INCOME) {

            List<TransactionResponse> incomeList = list.stream()
                    .map(t -> TransactionResponse.builder()
                            .transactionId(t.getId())
                            .amount(t.getAmount())
                            .type(t.getType())
                            .category(t.getCategory())
                            .date(t.getDate())
                            .note(t.getNote())
                            .userId(t.getUser().getId())
                            .build())
                    .toList();

            return incomeList;
        }

        return buildUserExpenseResponse(list);
    }

    @Override
    public List<TransactionResponse> getByCategory(String category) {

        UserEntity loggedUser = getLoggedUser();

        List<TransactionEntity> list;

        if (isAdmin(loggedUser)) {
            list = repository.findByCategoryAndDeletedFalse(category);
        } else {
            list = repository.findByCategoryAndUserAndDeletedFalse(category, loggedUser);
        }

        return list.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getById(Long id) {

        UserEntity loggedUser = getLoggedUser();

        TransactionEntity t;

        if (isAdmin(loggedUser)) {
            t = repository.findByIdAndDeletedFalse(id)
                    .filter(tx -> !tx.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        } else {
            t = repository.findByIdAndUserAndDeletedFalse(id, loggedUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        }

        return mapper.toResponse(t);
    }

    @Override
    public TransactionResponse update(Long id, UpdateTransactionRequest request) {

        UserEntity loggedUser = getLoggedUser();

        TransactionEntity t;

        if (isAdmin(loggedUser)) {
            t = repository.findByIdAndDeletedFalse(id)
                    .filter(tx -> !tx.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        } else {
            t = repository.findByIdAndUserAndDeletedFalse(id, loggedUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

            if (t.getType() == RecordType.INCOME) {
                throw new BadRequestException("You cannot edit income");
            }
        }

        if (request.getAmount() != null) t.setAmount(request.getAmount());
        if (request.getType() != null) t.setType(request.getType());
        if (request.getCategory() != null) t.setCategory(request.getCategory());
        if (request.getDate() != null) t.setDate(request.getDate());
        if (request.getNote() != null) t.setNote(request.getNote());

        validateTransaction(t.getType(), t.getCategory());

        return mapper.toResponse(repository.save(t));
    }

    @Override
    public void delete(Long id) {

        UserEntity loggedUser = getLoggedUser();

        TransactionEntity t;

        if (isAdmin(loggedUser)) {
            t = repository.findByIdAndDeletedFalse(id)
                    .filter(tx -> !tx.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        } else {
            t = repository.findByIdAndUserAndDeletedFalse(id, loggedUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        }

        t.setDeleted(true);
        repository.save(t);

        t.setDeleted(true);
        repository.save(t);
    }
    
    // ========== IS ADMIN CHECK ==========
    private boolean isAdmin(UserEntity user) {
        return user.getRole() == Role.ADMIN;
    }

    // ========== VALIDATE TRANSACTION ==========
    private void validateTransaction(RecordType type, String category) {

        if (type == RecordType.INCOME &&
                !category.equalsIgnoreCase("Salary")) {
            throw new BadRequestException("Income must be Salary");
        }

        if (type == RecordType.EXPENSE &&
                category.equalsIgnoreCase("Salary")) {
            throw new BadRequestException("Expense cannot be Salary");
        }
    }
    
    // ========== BUILD USER EXPENSE RESPONSE ==========
    private List<UserExpenseResponse> buildUserExpenseResponse(List<TransactionEntity> list) {

        return list.stream()
                .collect(Collectors.groupingBy(TransactionEntity::getUser))
                .entrySet()
                .stream()
                .map(entry -> {

                    UserEntity user = entry.getKey();

                    List<ExpenseResponse> expenses = entry.getValue().stream()
                            .filter(t -> t.getType() == RecordType.EXPENSE)
                            .map(t -> ExpenseResponse.builder()
                                    .amount(t.getAmount())
                                    .category(t.getCategory())
                                    .date(t.getDate())
                                    .note(t.getNote())
                                    .build())
                            .toList();

                    return UserExpenseResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .salary(null)
                            .expenses(expenses)
                            .build();
                })
                .toList();
    }
}