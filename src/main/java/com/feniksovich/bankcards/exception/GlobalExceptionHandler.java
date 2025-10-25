package com.feniksovich.bankcards.exception;

import com.feniksovich.bankcards.dto.validation.ErrorResponse;
import com.feniksovich.bankcards.dto.validation.ValidationErrorResponse;
import com.feniksovich.bankcards.dto.validation.Violation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Глобальный обработчик исключений для REST API.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Validation exceptions

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException ex) {
        log.debug("Constraint validation error: {}", ex.getMessage());
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(v -> new Violation(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.debug("Method argument validation error: {}", ex.getMessage());
        final List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList();
        return new ValidationErrorResponse(violations);
    }

    // Business logic exceptions

    @ExceptionHandler(CardOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onCardOperationException(CardOperationException ex) {
        log.debug("Card operation error: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error("Card Operation Error")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onResourceNotFoundException(ResourceNotFoundException ex) {
        log.debug("Resource not found: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .error("Resource Not Found")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceConflictException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onResourceConflictException(ResourceConflictException ex) {
        log.debug("Resource conflict: {}", ex.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .error("Resource Conflict")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> onResponseStatusException(ResponseStatusException ex) {
        log.debug("Response status exception: {}", ex.getMessage());
        final ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder();

        if (ex.getReason() != null) {
            builder.message(ex.getReason());
        }

        builder.status(HttpStatus.valueOf(ex.getStatusCode().value()));
        builder.message(ex.getMessage());

        return new ResponseEntity<>(builder.build(), ex.getStatusCode());
    }
}
