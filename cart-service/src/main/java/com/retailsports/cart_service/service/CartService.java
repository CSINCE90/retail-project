package com.retailsports.cart_service.service;

import com.retailsports.cart_service.dto.request.AddToCartRequest;
import com.retailsports.cart_service.dto.request.UpdateCartItemRequest;
import com.retailsports.cart_service.dto.response.CartResponse;
import com.retailsports.cart_service.dto.response.CartSummaryResponse;

/**
 * Service interface per la gestione del carrello
 */
public interface CartService {

    /**
     * Ottiene il carrello di un utente (crea se non esiste)
     */
    CartResponse getCart(Long userId);

    /**
     * Aggiunge un prodotto al carrello
     */
    CartResponse addToCart(Long userId, AddToCartRequest request);

    /**
     * Aggiorna la quantità di un item nel carrello
     */
    CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request);

    /**
     * Rimuove un item dal carrello
     */
    CartResponse removeFromCart(Long userId, Long productId);

    /**
     * Svuota completamente il carrello
     */
    void clearCart(Long userId);

    /**
     * Ottiene il riepilogo del carrello (senza dettagli items)
     */
    CartSummaryResponse getCartSummary(Long userId);
}
