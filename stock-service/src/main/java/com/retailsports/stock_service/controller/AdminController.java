package com.retailsports.stock_service.controller;

import com.retailsports.stock_service.dto.request.CreateStockRequest;
import com.retailsports.stock_service.dto.request.UpdateMinimumQuantityRequest;
import com.retailsports.stock_service.dto.response.ApiResponse;
import com.retailsports.stock_service.dto.response.LowStockAlertResponse;
import com.retailsports.stock_service.dto.response.StockResponse;
import com.retailsports.stock_service.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per operazioni admin sullo stock
 */
@RestController
@RequestMapping("/api/admin/stock")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final StockService stockService;

    /**
     * Crea stock per nuovo prodotto
     * POST /api/admin/stock
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StockResponse>> createStock(
            @Valid @RequestBody CreateStockRequest request
    ) {
        log.info("POST /api/admin/stock - Create stock for product {}", request.getProductId());
        StockResponse response = stockService.createStock(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock created successfully", response));
    }

    /**
     * Aggiorna soglia minima
     * PUT /api/admin/stock/{productId}/minimum
     */
    @PutMapping("/{productId}/minimum")
    public ResponseEntity<ApiResponse<StockResponse>> updateMinimumQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateMinimumQuantityRequest request
    ) {
        log.info("PUT /api/admin/stock/{}/minimum - Update minimum quantity", productId);
        StockResponse response = stockService.updateMinimumQuantity(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Minimum quantity updated successfully", response));
    }

    /**
     * Alert scorte basse attivi
     * GET /api/admin/stock/alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<LowStockAlertResponse>> getActiveLowStockAlerts() {
        log.info("GET /api/admin/stock/alerts - Get active low stock alerts");
        List<LowStockAlertResponse> response = stockService.getActiveLowStockAlerts();
        return ResponseEntity.ok(response);
    }
}
