package com.retailsports.stock_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per richiesta di creazione stock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockRequest {

    /**
     * ID del prodotto
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Quantità iniziale
     */
    @NotNull(message = "Initial quantity is required")
    @Min(value = 0, message = "Initial quantity cannot be negative")
    private Integer initialQuantity;

    /**
     * Quantità minima per alert
     */
    @Min(value = 0, message = "Minimum quantity cannot be negative")
    private Integer minimumQuantity = 10;
}
