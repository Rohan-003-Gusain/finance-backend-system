package com.finance.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.finance.dto.request.UserRequest;
import com.finance.dto.response.UserResponse;
import com.finance.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

	// ========== ENTITY TO RESPONSE ==========
	@Mapping(source = "id", target = "userId")
    UserResponse toResponse(UserEntity userEntity);

    // ========== REQUEST TO ENTITY ==========
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserEntity toEntity(UserRequest request);
}
