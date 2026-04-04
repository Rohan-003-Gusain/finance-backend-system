package com.finance.dto.request;

import lombok.Data;
import com.finance.model.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateUserRoleRequest {

	@NotNull(message = "Role is required")
	@Schema(description = "Role can be ADMIN or ANALYST or VIEWER", example = "ADMIN")	
    private Role role;
}