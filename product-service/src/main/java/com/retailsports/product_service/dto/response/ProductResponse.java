package com.retailsports.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    // Identificatori
    private Long id;
    private String sku;
    private String barcode;

    // Dati base
    private String name;
    private String slug;
    private String description;
    private String longDescription;

    // Relazioni
    private CategoryResponse category;
    private BrandResponse brand;

    // Prezzi (in centesimi)
    private Integer priceCents;
    private Integer compareAtPriceCents;
    private Integer costPriceCents;

    // Prezzi formattati (opzionali, calcolati)
    private String priceFormatted; // es. "99.99€"
    private String compareAtPriceFormatted;
    private BigDecimal discountPercentage; // Percentuale sconto se presente

    // Caratteristiche fisiche
    private Integer weightGrams;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;

    // Inventario
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean trackInventory;
    private Boolean isLowStock; // Calcolato
    private Boolean isOutOfStock; // Calcolato

    // Stato prodotto
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean isNew;
    private Boolean isOnSale;

    // SEO
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;

    // Statistiche
    private Integer viewsCount;
    private Integer salesCount;
    private BigDecimal ratingAverage;
    private Integer ratingCount;

    // Timestamp
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relazioni complesse
    private List<ProductImageResponse> images;
    private ProductImageResponse primaryImage; // Immagine principale estratta
    private List<AttributeValueResponse> attributes;
    private List<TagResponse> tags;
    private List<DiscountResponse> activeDiscounts; // Solo sconti validi
}
