package com.feniksovich.bankcards.exception;

/**
 * Исключение конфликта данных.
 * Соответствует статуту HTTP 409 (Conflict).
 */
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
