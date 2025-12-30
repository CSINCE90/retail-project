package com.retailsports.cart_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entità Cart rappresenta il carrello di un utente
 */
@Entity
@Table(name = "carts", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID dell'utente proprietario del carrello
     * NOTA: Soft relationship - non usiamo @ManyToOne perché user-service è un DB separato
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * Items nel carrello
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    /**
     * Timestamp di creazione
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp di ultimo aggiornamento
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    /**
     * Aggiunge un item al carrello
     */
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    /**
     * Rimuove un item dal carrello
     */
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    /**
     * Trova un item per productId
     */
    public CartItem findItemByProductId(Long productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Verifica se il carrello contiene un prodotto
     */
    public boolean hasProduct(Long productId) {
        return findItemByProductId(productId) != null;
    }

    /**
     * Svuota il carrello
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calcola il numero totale di items
     */
    public int getTotalItems() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    /**
     * Calcola il subtotale del carrello (somma di tutti i subtotal degli items)
     */
    public Long calculateSubtotal() {
        return items.stream()
            .mapToLong(CartItem::calculateSubtotal)
            .sum();
    }

    /**
     * Calcola il totale degli sconti
     */
    public Long calculateTotalDiscount() {
        return items.stream()
            .mapToLong(CartItem::calculateDiscountAmount)
            .sum();
    }

    /**
     * Calcola il totale finale del carrello
     */
    public Long calculateTotal() {
        return items.stream()
            .mapToLong(CartItem::calculateFinalPrice)
            .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;
        Cart cart = (Cart) o;
        return id != null && id.equals(cart.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
