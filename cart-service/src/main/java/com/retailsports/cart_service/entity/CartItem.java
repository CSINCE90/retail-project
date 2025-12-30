package com.retailsports.cart_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Entità CartItem rappresenta un singolo item nel carrello
 */
@Entity
@Table(name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
    },
    indexes = {
        @Index(name = "idx_product_id", columnList = "product_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Carrello a cui appartiene l'item
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /**
     * ID del prodotto
     * NOTA: Soft relationship - non usiamo @ManyToOne perché product-service è un DB separato
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Quantità del prodotto
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Prezzo unitario in centesimi (snapshot al momento dell'aggiunta)
     * IMPORTANTE: Salviamo il prezzo al momento dell'aggiunta, non ricalcoliamo dinamicamente
     */
    @Column(name = "unit_price_cents", nullable = false)
    private Long unitPriceCents;

    /**
     * Percentuale di sconto applicata
     */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    /**
     * Timestamp di aggiunta al carrello
     */
    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    /**
     * Timestamp di ultimo aggiornamento
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== BUSINESS LOGIC METHODS ==========

    /**
     * Calcola il subtotale (prezzo unitario * quantità)
     */
    public Long calculateSubtotal() {
        return unitPriceCents * quantity;
    }

    /**
     * Calcola l'ammontare dello sconto
     */
    public Long calculateDiscountAmount() {
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return 0L;
        }

        Long subtotal = calculateSubtotal();
        BigDecimal discountFactor = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal discountAmount = BigDecimal.valueOf(subtotal).multiply(discountFactor);

        return discountAmount.setScale(0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * Calcola il prezzo finale (subtotale - sconto)
     */
    public Long calculateFinalPrice() {
        return calculateSubtotal() - calculateDiscountAmount();
    }

    /**
     * Incrementa la quantità
     */
    public void incrementQuantity(int amount) {
        this.quantity += amount;
    }

    /**
     * Aggiorna la quantità
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = newQuantity;
    }

    /**
     * Applica uno sconto percentuale
     */
    public void applyDiscount(BigDecimal discountPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem cartItem = (CartItem) o;
        return id != null && id.equals(cartItem.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
