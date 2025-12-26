package com.retailsports.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private Boolean isActive;
    private String metaTitle;
    private String metaDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Contatore prodotti (opzionale)
    private Long productCount;
}
