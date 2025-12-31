package com.retailsports.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String productImage;

    // Quantità e prezzi
    private Integer quantity;
    private Long unitPriceCents;
    private BigDecimal discountPercentage;
    private Long discountCents;
    private Long subtotalCents;
    private Long totalCents;

    // Prezzi formattati
    private String unitPriceFormatted;
    private String totalFormatted;

    // Timestamp
    private LocalDateTime createdAt;
}
