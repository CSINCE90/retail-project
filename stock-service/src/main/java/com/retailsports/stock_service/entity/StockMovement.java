package com.retailsports.stock_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entità StockMovement rappresenta un movimento di stock (storico)
 */
@Entity
@Table(name = "stock_movements", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_movement_type", columnList = "movement_type"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_reference", columnList = "reference_type, reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del prodotto
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Tipo di movimento
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 20)
    private MovementType movementType;

    /**
     * Quantità movimento (sempre positivo)
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Quantità prima del movimento
     */
    @Column(name = "previous_quantity", nullable = false)
    private Integer previousQuantity;

    /**
     * Quantità dopo il movimento
     */
    @Column(name = "new_quantity", nullable = false)
    private Integer newQuantity;

    /**
     * Tipo di riferimento (ordine, acquisto, etc)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 20)
    private ReferenceType referenceType;

    /**
     * ID del riferimento
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * Note aggiuntive
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * ID dell'utente che ha creato il movimento
     */
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    /**
     * Timestamp di creazione
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========== ENUMS ==========

    /**
     * Tipi di movimento stock
     */
    public enum MovementType {
        IN,             // Carico merce (acquisto fornitore)
        OUT,            // Scarico merce (vendita)
        RESERVE,        // Prenotazione stock
        RELEASE,        // Rilascio prenotazione
        TRANSFER,       // Trasferimento tra magazzini
        ADJUSTMENT,     // Aggiustamento manuale (inventario)
        RETURN          // Reso cliente
    }

    /**
     * Tipi di riferimento
     */
    public enum ReferenceType {
        ORDER,          // Riferimento a ordine
        PURCHASE,       // Riferimento ad acquisto fornitore
        MANUAL,         // Operazione manuale
        TRANSFER,       // Trasferimento
        RETURN          // Reso
    }

    // ========== VALIDATION ==========

    @PrePersist
    private void validateMovement() {
        if (this.quantity <= 0) {
            throw new IllegalStateException("Movement quantity must be positive");
        }
        
        if (this.previousQuantity < 0 || this.newQuantity < 0) {
            throw new IllegalStateException("Quantities cannot be negative");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockMovement)) return false;
        StockMovement movement = (StockMovement) o;
        return id != null && id.equals(movement.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
