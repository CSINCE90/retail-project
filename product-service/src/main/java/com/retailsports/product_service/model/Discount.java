package com.retailsports.product_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "discounts", indexes = {
    @Index(name = "idx_code", columnList = "code"),
    @Index(name = "idx_active", columnList = "is_active"),
    @Index(name = "idx_dates", columnList = "starts_at, ends_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DiscountType type = DiscountType.PERCENTAGE;

    @Column(nullable = false)
    private Integer value;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "max_uses_per_user")
    @Builder.Default
    private Integer maxUsesPerUser = 1;

    @Column(name = "current_uses")
    @Builder.Default
    private Integer currentUses = 0;

    @Column(name = "min_purchase_amount_cents")
    private Integer minPurchaseAmountCents;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "discounts")
    @Builder.Default
    private Set<Product> products = new HashSet<>();

    // Enum per il tipo di sconto
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    // Business logic helpers
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return this.isActive
            && now.isAfter(this.startsAt)
            && now.isBefore(this.endsAt)
            && (this.maxUses == null || this.currentUses < this.maxUses);
    }

    public void incrementUsage() {
        this.currentUses++;
    }

    public boolean hasReachedMaxUses() {
        return this.maxUses != null && this.currentUses >= this.maxUses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discount)) return false;
        Discount discount = (Discount) o;
        return id != null && id.equals(discount.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
