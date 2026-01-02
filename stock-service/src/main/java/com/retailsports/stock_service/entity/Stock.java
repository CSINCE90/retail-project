package com.retailsports.stock_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entità Stock rappresenta la giacenza di un prodotto
 */
@Entity
@Table(name = "stock", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_available_quantity", columnList = "available_quantity")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del prodotto
     * NOTA: Soft relationship - non usiamo @ManyToOne perché product-service è un DB separato
     */
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    /**
     * Quantità disponibile per la vendita
     */
    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    /**
     * Quantità prenotata (ordini pending)
     */
    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    /**
     * Quantità fisica totale = available + reserved
     */
    @Column(name = "physical_quantity", nullable = false)
    @Builder.Default
    private Integer physicalQuantity = 0;

    /**
     * Soglia minima per alert scorte basse
     */
    @Column(name = "minimum_quantity")
    @Builder.Default
    private Integer minimumQuantity = 10;

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
     * Calcola la quantità fisica dal disponibile e riservato
     */
    public Integer calculatePhysical() {
        return this.availableQuantity + this.reservedQuantity;
    }

    /**
     * Valida che physical = available + reserved
     */
    @PreUpdate
    @PrePersist
    private void validatePhysicalQuantity() {
        if (this.physicalQuantity != (this.availableQuantity + this.reservedQuantity)) {
            throw new IllegalStateException(
                String.format("Physical quantity mismatch: physical=%d, available=%d, reserved=%d",
                    this.physicalQuantity, this.availableQuantity, this.reservedQuantity)
            );
        }
        
        if (this.availableQuantity < 0) {
            throw new IllegalStateException("Available quantity cannot be negative");
        }
        
        if (this.reservedQuantity < 0) {
            throw new IllegalStateException("Reserved quantity cannot be negative");
        }
    }

    /**
     * Prenota stock (disponibile → riservato)
     */
    public void reserve(int quantity) {
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException(
                String.format("Insufficient available stock: requested=%d, available=%d",
                    quantity, this.availableQuantity)
            );
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
    }

    /**
     * Conferma prenotazione (riservato → fuori dal magazzino)
     */
    public void confirm(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException(
                String.format("Insufficient reserved stock: requested=%d, reserved=%d",
                    quantity, this.reservedQuantity)
            );
        }
        this.reservedQuantity -= quantity;
        this.physicalQuantity -= quantity;
    }

    /**
     * Rilascia prenotazione (riservato → disponibile)
     */
    public void release(int quantity) {
        if (this.reservedQuantity < quantity) {
            throw new IllegalStateException(
                String.format("Insufficient reserved stock: requested=%d, reserved=%d",
                    quantity, this.reservedQuantity)
            );
        }
        this.reservedQuantity -= quantity;
        this.availableQuantity += quantity;
    }

    /**
     * Aggiusta quantità disponibile (carico/scarico manuale)
     */
    public void adjustQuantity(int delta) {
        int newAvailable = this.availableQuantity + delta;
        if (newAvailable < 0) {
            throw new IllegalStateException(
                String.format("Adjustment would result in negative available quantity: current=%d, delta=%d",
                    this.availableQuantity, delta)
            );
        }
        this.availableQuantity = newAvailable;
        this.physicalQuantity = calculatePhysical();
    }

    /**
     * Verifica se lo stock è sotto la soglia minima
     */
    public boolean isLowStock() {
        return this.availableQuantity < this.minimumQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock stock = (Stock) o;
        return id != null && id.equals(stock.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
