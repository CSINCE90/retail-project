package com.retailsports.cart_service.service;

import com.retailsports.cart_service.dto.request.AddToCartRequest;
import com.retailsports.cart_service.dto.request.UpdateCartItemRequest;
import com.retailsports.cart_service.dto.response.CartItemResponse;
import com.retailsports.cart_service.dto.response.CartResponse;
import com.retailsports.cart_service.dto.response.CartSummaryResponse;
import com.retailsports.cart_service.entity.Cart;
import com.retailsports.cart_service.entity.CartItem;
import com.retailsports.cart_service.exception.CartNotFoundException;
import com.retailsports.cart_service.repository.CartItemRepository;
import com.retailsports.cart_service.repository.CartRepository;
import com.retailsports.cart_service.service.ProductServiceClient.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del CartService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        log.info("Getting cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        return convertToCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        log.info("Adding product {} to cart for user {}", request.getProductId(), userId);

        // Valida stock tramite Product Service
        productServiceClient.validateProductStock(request.getProductId(), request.getQuantity());

        // Recupera info prodotto per il prezzo
        ProductInfo productInfo = productServiceClient.getProduct(request.getProductId());

        // Ottieni o crea carrello
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        // Verifica se il prodotto è già nel carrello
        CartItem existingItem = cart.findItemByProductId(request.getProductId());

        if (existingItem != null) {
            // Se già presente, aggiorna la quantità
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Valida lo stock per la nuova quantità totale
            productServiceClient.validateProductStock(request.getProductId(), newQuantity);

            existingItem.updateQuantity(newQuantity);
            log.info("Updated quantity for product {} in cart. New quantity: {}", request.getProductId(), newQuantity);
        } else {
            // Altrimenti crea un nuovo item
            CartItem newItem = CartItem.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .unitPriceCents(productInfo.getPriceCents().longValue())
                    .discountPercentage(BigDecimal.ZERO)
                    .build();

            cart.addItem(newItem);
            log.info("Added new product {} to cart", request.getProductId());
        }

        Cart savedCart = cartRepository.save(cart);
        return convertToCartResponse(savedCart);
    }

    @Override
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
        log.info("Updating cart item for user {} and product {}", userId, productId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem item = cart.findItemByProductId(productId);
        if (item == null) {
            throw new CartNotFoundException("Product " + productId + " not found in cart");
        }

        // Valida stock per la nuova quantità
        productServiceClient.validateProductStock(productId, request.getQuantity());

        // Aggiorna quantità
        item.updateQuantity(request.getQuantity());

        Cart savedCart = cartRepository.save(cart);
        log.info("Cart item updated successfully");

        return convertToCartResponse(savedCart);
    }

    @Override
    public CartResponse removeFromCart(Long userId, Long productId) {
        log.info("Removing product {} from cart for user {}", productId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem item = cart.findItemByProductId(productId);
        if (item == null) {
            throw new CartNotFoundException("Product " + productId + " not found in cart");
        }

        cart.removeItem(item);

        Cart savedCart = cartRepository.save(cart);
        log.info("Product {} removed from cart", productId);

        return convertToCartResponse(savedCart);
    }

    @Override
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        cart.clear();
        cartRepository.save(cart);

        log.info("Cart cleared for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(Long userId) {
        log.info("Getting cart summary for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        return convertToCartSummaryResponse(cart);
    }

    // ========== HELPER METHODS ==========

    /**
     * Crea un nuovo carrello per l'utente
     */
    private Cart createCart(Long userId) {
        log.info("Creating new cart for user: {}", userId);

        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        return cartRepository.save(cart);
    }

    /**
     * Converte Cart entity in CartResponse
     */
    private CartResponse convertToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());

        Long subtotal = cart.calculateSubtotal();
        Long totalDiscount = cart.calculateTotalDiscount();
        Long total = cart.calculateTotal();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .totalItems(cart.getTotalItems())
                .subtotalCents(subtotal)
                .totalDiscountCents(totalDiscount)
                .totalCents(total)
                .subtotalFormatted(formatPrice(subtotal))
                .totalDiscountFormatted(formatPrice(totalDiscount))
                .totalFormatted(formatPrice(total))
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    /**
     * Converte CartItem entity in CartItemResponse
     */
    private CartItemResponse convertToCartItemResponse(CartItem item) {
        Long subtotal = item.calculateSubtotal();
        Long discountAmount = item.calculateDiscountAmount();
        Long finalPrice = item.calculateFinalPrice();

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPriceCents(item.getUnitPriceCents())
                .discountPercentage(item.getDiscountPercentage())
                .subtotalCents(subtotal)
                .discountAmountCents(discountAmount)
                .finalPriceCents(finalPrice)
                .unitPriceFormatted(formatPrice(item.getUnitPriceCents()))
                .subtotalFormatted(formatPrice(subtotal))
                .finalPriceFormatted(formatPrice(finalPrice))
                .addedAt(item.getAddedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    /**
     * Converte Cart entity in CartSummaryResponse
     */
    private CartSummaryResponse convertToCartSummaryResponse(Cart cart) {
        Long subtotal = cart.calculateSubtotal();
        Long totalDiscount = cart.calculateTotalDiscount();
        Long total = cart.calculateTotal();

        return CartSummaryResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .totalItems(cart.getTotalItems())
                .uniqueProducts(cart.getItems().size())
                .subtotalCents(subtotal)
                .totalDiscountCents(totalDiscount)
                .totalCents(total)
                .subtotalFormatted(formatPrice(subtotal))
                .totalDiscountFormatted(formatPrice(totalDiscount))
                .totalFormatted(formatPrice(total))
                .build();
    }

    /**
     * Formatta prezzo in centesimi in stringa (es. 9999 -> "99.99€")
     */
    private String formatPrice(Long priceCents) {
        if (priceCents == null) return "0.00€";
        BigDecimal price = BigDecimal.valueOf(priceCents).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return String.format("%.2f€", price);
    }
}
