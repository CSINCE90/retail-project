package com.retailsports.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO lightweight per liste di prodotti (catalogo, ricerca, ecc.)
 * Include solo i campi essenziali per performance ottimali
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSummaryResponse {

    private Long id;
    private String sku;
    private String name;
    private String slug;

    // Prezzo
    private Integer priceCents;
    private Integer compareAtPriceCents;
    private String priceFormatted;
    private BigDecimal discountPercentage;

    // Brand e categoria (solo info essenziali)
    private String brandName;
    private String categoryName;

    // Immagine principale
    private String primaryImageUrl;

    // Rating
    private BigDecimal ratingAverage;
    private Integer ratingCount;

    // Flags
    private Boolean isFeatured;
    private Boolean isNew;
    private Boolean isOnSale;
    private Boolean isInStock;

    // Badge/Label calcolati (opzionale)
    private String badge; // es. "NEW", "SALE", "OUT OF STOCK"
}
