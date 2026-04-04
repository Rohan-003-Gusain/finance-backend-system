package com.finance.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
	
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	@Schema(example = "name@gmail.com")
	private String email;
	
	@NotBlank(message = "Password is required")
	@Schema(example = "name12345")
	private String password;
}
