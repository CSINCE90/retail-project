package com.retailsports.cart_service.exception;

/**
 * Eccezione lanciata quando lo stock di un prodotto è insufficiente
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
