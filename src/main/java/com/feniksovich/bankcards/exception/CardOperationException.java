package com.feniksovich.bankcards.exception;

/**
 * Исключение, сигнализирующее о невозможности операции с картой.
 */
public class CardOperationException extends RuntimeException {
    public CardOperationException(String message) {
        super(message);
    }
}
