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
public class ProductImageResponse {

    private Long id;
    private String imageUrl;
    private String altText;
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
