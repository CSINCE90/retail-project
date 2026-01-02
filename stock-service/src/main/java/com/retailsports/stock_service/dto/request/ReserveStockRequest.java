package com.retailsports.stock_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per richiesta di prenotazione stock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveStockRequest {

    /**
     * ID del prodotto
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * ID dell'ordine
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * Quantità da prenotare
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
