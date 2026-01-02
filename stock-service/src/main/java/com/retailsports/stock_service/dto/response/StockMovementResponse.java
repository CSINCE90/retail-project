package com.retailsports.stock_service.dto.response;

import com.retailsports.stock_service.entity.StockMovement.MovementType;
import com.retailsports.stock_service.entity.StockMovement.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO per risposta StockMovement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;
    private Long productId;
    private MovementType movementType;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private ReferenceType referenceType;
    private Long referenceId;
    private String notes;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}
