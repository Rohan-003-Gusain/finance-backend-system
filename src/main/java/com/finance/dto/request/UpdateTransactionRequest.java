package com.finance.dto.request;

import lombok.Data;
import java.time.LocalDate;
import com.finance.model.enums.RecordType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Data
public class UpdateTransactionRequest {

	@Positive(message = "Amount must be greater than 0")
	@Schema(example = "5000")
    private Double amount;
	
    private RecordType type;
    
    @Schema(example = "Food")
    private String category;
    
    @PastOrPresent(message = "Date cannot be in future")
    private LocalDate date;
    
    private String note;
}