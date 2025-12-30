package com.retailsports.cart_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO per aggiungere un prodotto al carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {

    /**
     * ID del prodotto da aggiungere
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Quantità da aggiungere
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
