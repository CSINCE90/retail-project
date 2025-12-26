package com.retailsports.product_service.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {

    // Filtri categoria e brand
    private Long categoryId;
    private Long brandId;

    // Filtri prezzo (in centesimi)
    @Min(value = 0, message = "Min price must be at least 0")
    private Integer minPrice;

    @Min(value = 0, message = "Max price must be at least 0")
    private Integer maxPrice;

    // Ricerca full-text
    private String keyword;

    // Filtri stato prodotto
    private Boolean featured;
    private Boolean newArrivals;
    private Boolean onSale;
    private Boolean inStock;

    // Filtri attributi (es. ["1", "5", "12"] - IDs di AttributeValue)
    private List<Long> attributeValueIds;

    // Filtri tags
    private List<Long> tagIds;

    // Filtri rating
    @Min(value = 0, message = "Min rating must be at least 0")
    private Integer minRating;

    // Ordinamento
    private String sortBy; // price_asc, price_desc, newest, popular, rating, name_asc, name_desc

    // Paginazione
    @Min(value = 0, message = "Page must be at least 0")
    @Builder.Default
    private Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Builder.Default
    private Integer size = 20;
}
