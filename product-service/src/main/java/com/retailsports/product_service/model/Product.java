package com.retailsports.product_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_sku", columnList = "sku"),
    @Index(name = "idx_slug", columnList = "slug"),
    @Index(name = "idx_category", columnList = "category_id"),
    @Index(name = "idx_brand", columnList = "brand_id"),
    @Index(name = "idx_active", columnList = "is_active"),
    @Index(name = "idx_featured", columnList = "is_featured"),
    @Index(name = "idx_deleted", columnList = "deleted_at"),
    @Index(name = "idx_price", columnList = "price_cents"),
    @Index(name = "idx_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Codici identificativi
    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    @Column(unique = true, length = 50)
    private String barcode;

    // Dati base
    @Column(nullable = false, length = 255)
    private String name;

    @Column(unique = true, nullable = false, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "long_description", columnDefinition = "LONGTEXT")
    private String longDescription;

    // Relazioni
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // Prezzi (in centesimi per evitare problemi con float)
    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Column(name = "compare_at_price_cents")
    private Integer compareAtPriceCents;

    @Column(name = "cost_price_cents")
    private Integer costPriceCents;

    // Caratteristiche fisiche
    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "length_cm", precision = 10, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 10, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 10, scale = 2)
    private BigDecimal heightCm;

    // Inventario (snapshot - il dettaglio è in Stock Service)
    @Column(name = "stock_quantity")
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "low_stock_threshold")
    @Builder.Default
    private Integer lowStockThreshold = 10;

    @Column(name = "track_inventory", nullable = false)
    @Builder.Default
    private Boolean trackInventory = true;

    // Stato prodotto
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_new", nullable = false)
    @Builder.Default
    private Boolean isNew = false;

    @Column(name = "is_on_sale", nullable = false)
    @Builder.Default
    private Boolean isOnSale = false;

    // SEO
    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    // Statistiche (denormalized per performance)
    @Column(name = "views_count")
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(name = "sales_count")
    @Builder.Default
    private Integer salesCount = 0;

    @Column(name = "rating_average", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAverage = BigDecimal.ZERO;

    @Column(name = "rating_count")
    @Builder.Default
    private Integer ratingCount = 0;

    // Soft delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Timestamp automatici
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relazioni One-to-Many
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductAttributeValue> attributeValues = new HashSet<>();

    // Relazioni Many-to-Many
    @ManyToMany
    @JoinTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "product_discounts",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "discount_id")
    )
    @Builder.Default
    private Set<Discount> discounts = new HashSet<>();

    // Helper methods per gestire le relazioni
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }

    public void addAttributeValue(ProductAttributeValue attributeValue) {
        attributeValues.add(attributeValue);
        attributeValue.setProduct(this);
    }

    public void removeAttributeValue(ProductAttributeValue attributeValue) {
        attributeValues.remove(attributeValue);
        attributeValue.setProduct(null);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getProducts().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getProducts().remove(this);
    }

    public void addDiscount(Discount discount) {
        discounts.add(discount);
        discount.getProducts().add(this);
    }

    public void removeDiscount(Discount discount) {
        discounts.remove(discount);
        discount.getProducts().remove(this);
    }

    // Soft delete methods
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    public void restore() {
        this.deletedAt = null;
        this.isActive = true;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // Business logic helpers
    public boolean isLowStock() {
        return this.trackInventory && this.stockQuantity <= this.lowStockThreshold;
    }

    public void incrementViews() {
        this.viewsCount++;
    }

    public void incrementSales() {
        this.salesCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
