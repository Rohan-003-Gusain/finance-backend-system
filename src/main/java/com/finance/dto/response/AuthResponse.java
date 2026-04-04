package com.finance.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

	@Schema(example = "jwt-token-example")
	private String token;

	@Schema(example = "Bearer")
	private String tokenType;

	@Schema(example = "Login successful")
	private String message;
}