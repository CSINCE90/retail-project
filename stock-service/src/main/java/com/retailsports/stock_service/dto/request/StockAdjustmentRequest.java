package com.retailsports.stock_service.dto.request;

import com.retailsports.stock_service.entity.StockMovement.MovementType;
import com.retailsports.stock_service.entity.StockMovement.ReferenceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per richiesta di aggiustamento stock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequest {

    /**
     * Tipo di movimento (IN, OUT, ADJUSTMENT)
     */
    @NotNull(message = "Movement type is required")
    private MovementType movementType;

    /**
     * Quantità da aggiungere/rimuovere
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * Tipo di riferimento (opzionale)
     */
    private ReferenceType referenceType;

    /**
     * ID del riferimento (opzionale)
     */
    private Long referenceId;

    /**
     * Note aggiuntive (opzionale)
     */
    private String notes;

    /**
     * ID utente che esegue l'operazione (opzionale)
     */
    private Long userId;
}
