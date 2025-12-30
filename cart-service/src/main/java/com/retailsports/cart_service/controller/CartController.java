package com.retailsports.cart_service.controller;

import com.retailsports.cart_service.dto.request.AddToCartRequest;
import com.retailsports.cart_service.dto.request.UpdateCartItemRequest;
import com.retailsports.cart_service.dto.response.CartResponse;
import com.retailsports.cart_service.dto.response.CartSummaryResponse;
import com.retailsports.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller per la gestione del carrello
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/cart/{userId}
     * Ottiene il carrello completo di un utente
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        log.info("REST request to get cart for user: {}", userId);
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * POST /api/cart/{userId}/items
     * Aggiunge un prodotto al carrello
     */
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        log.info("REST request to add product {} to cart for user {}", request.getProductId(), userId);
        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    /**
     * PUT /api/cart/{userId}/items/{productId}
     * Aggiorna la quantità di un prodotto nel carrello
     */
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        log.info("REST request to update product {} in cart for user {}", productId, userId);
        CartResponse cart = cartService.updateCartItem(userId, productId, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * DELETE /api/cart/{userId}/items/{productId}
     * Rimuove un prodotto dal carrello
     */
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        log.info("REST request to remove product {} from cart for user {}", productId, userId);
        CartResponse cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }

    /**
     * DELETE /api/cart/{userId}
     * Svuota completamente il carrello
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        log.info("REST request to clear cart for user: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/cart/{userId}/summary
     * Ottiene il riepilogo del carrello (senza dettagli items)
     */
    @GetMapping("/{userId}/summary")
    public ResponseEntity<CartSummaryResponse> getCartSummary(@PathVariable Long userId) {
        log.info("REST request to get cart summary for user: {}", userId);
        CartSummaryResponse summary = cartService.getCartSummary(userId);
        return ResponseEntity.ok(summary);
    }
}
