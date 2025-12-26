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
public class TagResponse {

    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;

    // Contatore prodotti (opzionale)
    private Long productCount;
}
