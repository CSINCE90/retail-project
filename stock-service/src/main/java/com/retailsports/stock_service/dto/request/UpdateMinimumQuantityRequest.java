package com.retailsports.stock_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per richiesta di aggiornamento quantità minima
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMinimumQuantityRequest {

    /**
     * Nuova quantità minima
     */
    @NotNull(message = "Minimum quantity is required")
    @Min(value = 0, message = "Minimum quantity cannot be negative")
    private Integer minimumQuantity;
}
