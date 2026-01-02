package com.retailsports.stock_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entità StockReservation rappresenta una prenotazione di stock per un ordine
 */
@Entity
@Table(name = "stock_reservations", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del prodotto
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * ID dell'ordine
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * Quantità prenotata
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Status della prenotazione
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.ACTIVE;

    /**
     * Scadenza della prenotazione
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

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

    /**
     * Timestamp di conferma
     */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    /**
     * Timestamp di rilascio
     */
    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    // ========== ENUMS ==========

    /**
     * Status della prenotazione
     */
    public enum ReservationStatus {
        ACTIVE,         // Prenotazione attiva
        CONFIRMED,      // Prenotazione confermata (ordine pagato)
        RELEASED,       // Prenotazione rilasciata (ordine cancellato)
        EXPIRED         // Prenotazione scaduta (timeout)
    }

    // ========== HELPER METHODS ==========

    /**
     * Verifica se la prenotazione è scaduta
     */
    public boolean isExpired() {
        return this.expiresAt != null && LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Conferma la prenotazione
     */
    public void confirm() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE reservations can be confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * Rilascia la prenotazione
     */
    public void release() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE reservations can be released");
        }
        this.status = ReservationStatus.RELEASED;
        this.releasedAt = LocalDateTime.now();
    }

    /**
     * Marca la prenotazione come scaduta
     */
    public void expire() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE reservations can expire");
        }
        this.status = ReservationStatus.EXPIRED;
        this.releasedAt = LocalDateTime.now();
    }

    @PrePersist
    private void validateReservation() {
        if (this.quantity <= 0) {
            throw new IllegalStateException("Reservation quantity must be positive");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockReservation)) return false;
        StockReservation reservation = (StockReservation) o;
        return id != null && id.equals(reservation.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
