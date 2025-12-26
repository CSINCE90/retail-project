package com.retailsports.product_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attribute_values",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_attr_value", columnNames = {"attribute_id", "value"})
    },
    indexes = {
        @Index(name = "idx_attribute", columnList = "attribute_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductAttribute attribute;

    @Column(nullable = false, length = 100)
    private String value;

    @Column(name = "display_value", nullable = false, length = 100)
    private String displayValue;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeValue)) return false;
        AttributeValue that = (AttributeValue) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
