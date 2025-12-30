package com.retailsports.cart_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO per aggiornare la quantità di un item nel carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemRequest {

    /**
     * Nuova quantità
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
