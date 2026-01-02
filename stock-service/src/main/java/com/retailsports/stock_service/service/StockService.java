package com.retailsports.stock_service.service;

import com.retailsports.stock_service.dto.request.CreateStockRequest;
import com.retailsports.stock_service.dto.request.ReserveStockRequest;
import com.retailsports.stock_service.dto.request.StockAdjustmentRequest;
import com.retailsports.stock_service.dto.request.UpdateMinimumQuantityRequest;
import com.retailsports.stock_service.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface per la gestione dello stock
 */
public interface StockService {

    // ========== GESTIONE STOCK ==========
    
    /**
     * Ottieni stock per productId
     */
    StockResponse getStockByProductId(Long productId);
    
    /**
     * Ottieni tutti gli stock con paginazione
     */
    Page<StockResponse> getAllStock(Pageable pageable);
    
    /**
     * Aggiusta quantità stock (IN/OUT/ADJUSTMENT)
     */
    StockResponse adjustStock(Long productId, StockAdjustmentRequest request);
    
    /**
     * Ottieni storico movimenti per prodotto
     */
    Page<StockMovementResponse> getMovementsByProductId(Long productId, Pageable pageable);
    
    /**
     * Ottieni prodotti con scorte basse
     */
    List<StockResponse> getLowStockProducts();
    
    // ========== PRENOTAZIONI ==========
    
    /**
     * Prenota stock per ordine
     */
    ReservationResponse reserveStock(ReserveStockRequest request);
    
    /**
     * Conferma prenotazione (ordine pagato)
     */
    ReservationResponse confirmReservation(Long reservationId);
    
    /**
     * Rilascia prenotazione (ordine cancellato)
     */
    ReservationResponse releaseReservation(Long reservationId);
    
    /**
     * Ottieni prenotazioni per ordine
     */
    List<ReservationResponse> getReservationsByOrderId(Long orderId);
    
    // ========== ADMIN ==========
    
    /**
     * Crea stock per nuovo prodotto
     */
    StockResponse createStock(CreateStockRequest request);
    
    /**
     * Aggiorna soglia minima
     */
    StockResponse updateMinimumQuantity(Long productId, UpdateMinimumQuantityRequest request);
    
    /**
     * Ottieni alert scorte basse attivi
     */
    List<LowStockAlertResponse> getActiveLowStockAlerts();
}
