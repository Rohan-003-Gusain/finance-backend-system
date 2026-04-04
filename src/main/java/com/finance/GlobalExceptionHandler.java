package com.finance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.finance.dto.ApiErrorDTO;
import com.finance.exception.AccessDeniedException;
import com.finance.exception.BadRequestException;
import com.finance.exception.ConflictException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// ========== ERROR RESPONSE BUILDER ==========
	private ResponseEntity<ApiErrorDTO> buildError (
		HttpStatus status,
		String message,
		HttpServletRequest request) {
		
		ApiErrorDTO error = ApiErrorDTO.builder()
			.status(status.value())
			.error(status.name())
			.message(message)
			.path(request.getRequestURI())
			.build();
	
	return new ResponseEntity<>(error, status);
	}
	
	// ========== 400 BAD REQUEST ==========
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiErrorDTO> handleBadRequest(
			BadRequestException  ex, HttpServletRequest request) {	
			
		return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}
		
	// ========= 400 ILLEGAL ARGUMENT =========
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }
    
    // ========== 400 VALIDATION ERROR ==========
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }
    
    // ========== 401 UNAUTHORIZED ==========
 	@ExceptionHandler(UnauthorizedException.class)
 	public ResponseEntity<ApiErrorDTO> handleUnauthorized(
 			UnauthorizedException ex, HttpServletRequest request) {	
 		
 		return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
 	}
 	
 	// ========== 403 FORBIDDEN ==========
 	@ExceptionHandler(AccessDeniedException.class)
 	public ResponseEntity<ApiErrorDTO> handleAccessDenied(
 	        AccessDeniedException ex,
 	        HttpServletRequest request) {

 	    return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
 	}
	
	// ========== 404 NOTFOUND ==========
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorDTO> handleNotFound(
			ResourceNotFoundException ex, 
			HttpServletRequest request) {	
		
		return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}
	
	// ========== 409 CONFLICT ==========
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiErrorDTO> handleConflict(
			ConflictException ex, HttpServletRequest request) {	
		
		return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
	}
		
	// ========== 500 INTERNAL SERVER ERROR ==========
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiErrorDTO> handleRuntimeException(
			RuntimeException ex, HttpServletRequest request) {
			
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
	}

}
