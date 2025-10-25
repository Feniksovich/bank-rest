package com.feniksovich.bankcards.exception;

/**
 * Исключение отсутствия запрашиваемого ресурса.
 * Соответствует статуту HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
