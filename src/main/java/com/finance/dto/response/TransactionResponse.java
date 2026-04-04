package com.finance.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import com.finance.model.enums.RecordType;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
public class TransactionResponse {

	@Schema(example = "1")
	private Long transactionId;

	@Schema(example = "5000")
	private Double amount;

	@Schema(example = "INCOME")
	private RecordType type;

	@Schema(example = "Salary")
	private String category;
	
    private LocalDate date;
    private String note;
    
    @Schema(example = "1")
    private Long userId;
}