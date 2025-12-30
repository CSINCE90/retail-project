package com.retailsports.cart_service.exception;

/**
 * Eccezione lanciata quando un carrello non viene trovato
 */
public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
