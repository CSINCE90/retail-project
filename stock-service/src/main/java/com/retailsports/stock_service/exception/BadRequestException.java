package com.retailsports.stock_service.exception;

/**
 * Eccezione per richiesta non valida
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
