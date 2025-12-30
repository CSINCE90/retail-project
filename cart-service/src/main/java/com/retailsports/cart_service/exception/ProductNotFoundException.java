package com.retailsports.cart_service.exception;

/**
 * Eccezione lanciata quando un prodotto non viene trovato nel Product Service
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
