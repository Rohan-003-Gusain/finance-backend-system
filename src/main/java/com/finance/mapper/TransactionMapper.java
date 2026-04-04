package com.finance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.finance.dto.request.CreateTransactionRequest;
import com.finance.dto.response.TransactionResponse;
import com.finance.model.TransactionEntity;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

	// ========== REQUEST TO ENTITY ==========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    TransactionEntity toEntity(CreateTransactionRequest request);

    // ========== ENTITY TO RESPONSE ==========
    @Mapping(target = "userId", source = "user.id")
    @Mapping(source = "id", target = "transactionId")
    TransactionResponse toResponse(TransactionEntity transactionEntity);
}