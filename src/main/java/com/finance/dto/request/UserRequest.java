package com.finance.dto.request;


import com.finance.model.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
	
	@NotBlank(message = "Name is required")
    private String name;
	
	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
	@Schema(example = "name@gmail.com")
    private String email;
	
	@NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
	@Schema(example = "name12345")
	private String password;
	
	@NotNull(message = "Role is required")
	@Schema(description = "Role can be ADMIN or ANALYST or VIEWER", example = "ADMIN")
	private Role role;
    
}
