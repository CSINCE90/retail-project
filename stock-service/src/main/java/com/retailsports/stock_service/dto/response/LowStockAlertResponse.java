package com.retailsports.stock_service.dto.response;

import com.retailsports.stock_service.entity.LowStockAlert.AlertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO per risposta LowStockAlert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer availableQuantity;
    private Integer minimumQuantity;
    private AlertStatus alertStatus;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
