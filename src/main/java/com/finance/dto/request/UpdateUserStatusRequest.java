package com.finance.dto.request;

import lombok.Data;
import com.finance.model.enums.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateUserStatusRequest {

	@NotNull(message = "Status is required")
	@Schema(description = "Status can be ACTIVE or INACTIVE", example = "ACTIVE")
    private UserStatus status;
}