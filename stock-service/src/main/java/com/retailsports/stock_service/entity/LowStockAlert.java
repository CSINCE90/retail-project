package com.retailsports.stock_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entità LowStockAlert rappresenta un alert per scorte basse
 */
@Entity
@Table(name = "low_stock_alerts", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_alert_status", columnList = "alert_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del prodotto
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Quantità disponibile al momento dell'alert
     */
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    /**
     * Quantità minima configurata
     */
    @Column(name = "minimum_quantity", nullable = false)
    private Integer minimumQuantity;

    /**
     * Status dell'alert
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_status", nullable = false, length = 20)
    @Builder.Default
    private AlertStatus alertStatus = AlertStatus.ACTIVE;

    /**
     * Timestamp di creazione
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp di risoluzione
     */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // ========== ENUMS ==========

    /**
     * Status dell'alert
     */
    public enum AlertStatus {
        ACTIVE,         // Alert attivo
        RESOLVED        // Alert risolto (ricaricato stock)
    }

    // ========== HELPER METHODS ==========

    /**
     * Risolve l'alert
     */
    public void resolve() {
        this.alertStatus = AlertStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LowStockAlert)) return false;
        LowStockAlert alert = (LowStockAlert) o;
        return id != null && id.equals(alert.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
