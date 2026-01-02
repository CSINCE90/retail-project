package com.retailsports.stock_service.controller;

import com.retailsports.stock_service.dto.request.ReserveStockRequest;
import com.retailsports.stock_service.dto.request.StockAdjustmentRequest;
import com.retailsports.stock_service.dto.response.*;
import com.retailsports.stock_service.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller per la gestione dello stock
 */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    private final StockService stockService;

    /**
     * Ottieni stock per productId
     * GET /api/stock/{productId}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> getStockByProductId(@PathVariable Long productId) {
        log.info("GET /api/stock/{} - Get stock for product", productId);
        StockResponse response = stockService.getStockByProductId(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista tutti gli stock (con paginazione)
     * GET /api/stock?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<StockResponse>> getAllStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/stock - Get all stock (page={}, size={})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<StockResponse> response = stockService.getAllStock(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Aggiusta quantità stock (IN/OUT/ADJUSTMENT)
     * POST /api/stock/{productId}/adjust
     */
    @PostMapping("/{productId}/adjust")
    public ResponseEntity<ApiResponse<StockResponse>> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockAdjustmentRequest request
    ) {
        log.info("POST /api/stock/{}/adjust - Adjust stock", productId);
        StockResponse response = stockService.adjustStock(productId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", response));
    }

    /**
     * Storico movimenti prodotto
     * GET /api/stock/{productId}/movements?page=0&size=20
     */
    @GetMapping("/{productId}/movements")
    public ResponseEntity<Page<StockMovementResponse>> getMovements(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/stock/{}/movements - Get movements (page={}, size={})", productId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<StockMovementResponse> response = stockService.getMovementsByProductId(productId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Prodotti sotto scorta minima
     * GET /api/stock/low-stock
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockResponse>> getLowStockProducts() {
        log.info("GET /api/stock/low-stock - Get low stock products");
        List<StockResponse> response = stockService.getLowStockProducts();
        return ResponseEntity.ok(response);
    }

    /**
     * Prenota stock per ordine
     * POST /api/stock/reserve
     */
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<ReservationResponse>> reserveStock(
            @Valid @RequestBody ReserveStockRequest request
    ) {
        log.info("POST /api/stock/reserve - Reserve stock for order {}", request.getOrderId());
        ReservationResponse response = stockService.reserveStock(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock reserved successfully", response));
    }

    /**
     * Conferma prenotazione (ordine pagato)
     * POST /api/stock/confirm/{reservationId}
     */
    @PostMapping("/confirm/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirmReservation(
            @PathVariable Long reservationId
    ) {
        log.info("POST /api/stock/confirm/{} - Confirm reservation", reservationId);
        ReservationResponse response = stockService.confirmReservation(reservationId);
        return ResponseEntity.ok(ApiResponse.success("Reservation confirmed successfully", response));
    }

    /**
     * Rilascia prenotazione (ordine cancellato)
     * POST /api/stock/release/{reservationId}
     */
    @PostMapping("/release/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationResponse>> releaseReservation(
            @PathVariable Long reservationId
    ) {
        log.info("POST /api/stock/release/{} - Release reservation", reservationId);
        ReservationResponse response = stockService.releaseReservation(reservationId);
        return ResponseEntity.ok(ApiResponse.success("Reservation released successfully", response));
    }

    /**
     * Prenotazioni per ordine
     * GET /api/stock/reservations/{orderId}
     */
    @GetMapping("/reservations/{orderId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByOrderId(
            @PathVariable Long orderId
    ) {
        log.info("GET /api/stock/reservations/{} - Get reservations for order", orderId);
        List<ReservationResponse> response = stockService.getReservationsByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
