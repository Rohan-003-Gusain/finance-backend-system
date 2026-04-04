package com.finance.dto.response;

import lombok.Builder;
import lombok.Data;
import com.finance.model.enums.Role;
import com.finance.model.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
public class UserResponse {

	@Schema(example = "1")
	private Long userId;

	@Schema(example = "Rohan")
	private String name;

	@Schema(example = "ADMIN")
	private Role role;
	
	@Schema(example = "ACTIVE")
    private UserStatus status;
}