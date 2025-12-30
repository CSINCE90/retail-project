package com.retailsports.cart_service.exception;

/**
 * Eccezione lanciata quando la richiesta contiene dati non validi
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
