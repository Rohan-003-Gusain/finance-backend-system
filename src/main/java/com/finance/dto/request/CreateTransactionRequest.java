package com.finance.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import com.finance.model.enums.RecordType;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class CreateTransactionRequest {

	@Schema(example = "5000")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @Schema(description = "ADMIN: INCOME or EXPENSE | USER: only EXPENSE", example = "INCOME")
    @NotNull(message = "Type is required")
    private RecordType type;

    @Schema(description = "Category can be Salary or Fun or Travel or Anything", example = "Salary")
    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in future")
    private LocalDate date;

    @Schema(example = "Monthly salary")
    private String note;
    
    @Schema(example = "1")
    private Long userId;
}