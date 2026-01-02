package com.retailsports.stock_service.service;

import com.retailsports.stock_service.dto.request.CreateStockRequest;
import com.retailsports.stock_service.dto.request.ReserveStockRequest;
import com.retailsports.stock_service.dto.request.StockAdjustmentRequest;
import com.retailsports.stock_service.dto.request.UpdateMinimumQuantityRequest;
import com.retailsports.stock_service.dto.response.*;
import com.retailsports.stock_service.entity.*;
import com.retailsports.stock_service.entity.LowStockAlert.AlertStatus;
import com.retailsports.stock_service.entity.StockMovement.MovementType;
import com.retailsports.stock_service.entity.StockMovement.ReferenceType;
import com.retailsports.stock_service.entity.StockReservation.ReservationStatus;
import com.retailsports.stock_service.exception.*;
import com.retailsports.stock_service.repository.*;
import com.retailsports.stock_service.service.ProductServiceClient.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del StockService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockReservationRepository stockReservationRepository;
    private final LowStockAlertRepository lowStockAlertRepository;
    private final ProductServiceClient productServiceClient;

    @Value("${stock.reservation.expiration-minutes:30}")
    private int reservationExpirationMinutes;

    // ========== GESTIONE STOCK ==========

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockByProductId(Long productId) {
        log.info("Getting stock for product: {}", productId);

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product: " + productId));

        return convertToStockResponse(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockResponse> getAllStock(Pageable pageable) {
        log.info("Getting all stock with pagination");

        return stockRepository.findAll(pageable)
                .map(this::convertToStockResponse);
    }

    @Override
    public StockResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        log.info("Adjusting stock for product {}: type={}, quantity={}", 
            productId, request.getMovementType(), request.getQuantity());

        // Verifica che il prodotto esista
        productServiceClient.validateProduct(productId);

        // Ottieni o crea stock
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product: " + productId));

        int previousQuantity = stock.getAvailableQuantity();
        int delta;

        // Calcola il delta basato sul tipo di movimento
        switch (request.getMovementType()) {
            case IN:
                delta = request.getQuantity();
                stock.adjustQuantity(delta);
                break;
            case OUT:
                delta = -request.getQuantity();
                stock.adjustQuantity(delta);
                break;
            case ADJUSTMENT:
                // Per ADJUSTMENT, la quantity è il nuovo valore assoluto
                delta = request.getQuantity() - stock.getAvailableQuantity();
                stock.setAvailableQuantity(request.getQuantity());
                stock.setPhysicalQuantity(stock.calculatePhysical());
                break;
            default:
                throw new BadRequestException("Invalid movement type: " + request.getMovementType());
        }

        Stock savedStock = stockRepository.save(stock);
        log.info("Stock adjusted successfully for product {}", productId);

        // Crea movimento
        createMovement(
            productId,
            request.getMovementType(),
            request.getQuantity(),
            previousQuantity,
            savedStock.getAvailableQuantity(),
            request.getReferenceType(),
            request.getReferenceId(),
            request.getNotes(),
            request.getUserId()
        );

        // Gestisci alert scorte basse
        handleLowStockAlert(savedStock);

        return convertToStockResponse(savedStock);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementResponse> getMovementsByProductId(Long productId, Pageable pageable) {
        log.info("Getting stock movements for product: {}", productId);

        return stockMovementRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(this::convertToMovementResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getLowStockProducts() {
        log.info("Getting low stock products");

        return stockRepository.findLowStockProducts().stream()
                .map(this::convertToStockResponse)
                .collect(Collectors.toList());
    }

    // ========== PRENOTAZIONI ==========

    @Override
    public ReservationResponse reserveStock(ReserveStockRequest request) {
        log.info("Reserving stock for order {}: product={}, quantity={}", 
            request.getOrderId(), request.getProductId(), request.getQuantity());

        // Verifica che il prodotto esista
        productServiceClient.validateProduct(request.getProductId());

        // Ottieni stock
        Stock stock = stockRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Stock not found for product: " + request.getProductId()));

        // Verifica disponibilità
        if (stock.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for product %d. Available: %d, Requested: %d",
                    request.getProductId(), stock.getAvailableQuantity(), request.getQuantity()));
        }

        int previousQuantity = stock.getAvailableQuantity();

        // Prenota stock
        stock.reserve(request.getQuantity());
        Stock savedStock = stockRepository.save(stock);

        // Crea prenotazione
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(reservationExpirationMinutes);
        StockReservation reservation = StockReservation.builder()
                .productId(request.getProductId())
                .orderId(request.getOrderId())
                .quantity(request.getQuantity())
                .status(ReservationStatus.ACTIVE)
                .expiresAt(expiresAt)
                .build();

        StockReservation savedReservation = stockReservationRepository.save(reservation);
        log.info("Stock reserved successfully. Reservation ID: {}", savedReservation.getId());

        // Crea movimento RESERVE
        createMovement(
            request.getProductId(),
            MovementType.RESERVE,
            request.getQuantity(),
            previousQuantity,
            savedStock.getAvailableQuantity(),
            ReferenceType.ORDER,
            request.getOrderId(),
            "Stock reserved for order " + request.getOrderId(),
            null
        );

        return convertToReservationResponse(savedReservation);
    }

    @Override
    public ReservationResponse confirmReservation(Long reservationId) {
        log.info("Confirming reservation: {}", reservationId);

        StockReservation reservation = stockReservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE reservations can be confirmed. Current status: " + reservation.getStatus());
        }

        Stock stock = stockRepository.findByProductId(reservation.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Stock not found for product: " + reservation.getProductId()));

        int previousQuantity = stock.getPhysicalQuantity();

        // Conferma prenotazione
        stock.confirm(reservation.getQuantity());
        reservation.confirm();

        stockRepository.save(stock);
        StockReservation savedReservation = stockReservationRepository.save(reservation);
        log.info("Reservation confirmed successfully: {}", reservationId);

        // Crea movimento OUT
        createMovement(
            reservation.getProductId(),
            MovementType.OUT,
            reservation.getQuantity(),
            previousQuantity,
            stock.getPhysicalQuantity(),
            ReferenceType.ORDER,
            reservation.getOrderId(),
            "Order confirmed - reservation " + reservationId,
            null
        );

        return convertToReservationResponse(savedReservation);
    }

    @Override
    public ReservationResponse releaseReservation(Long reservationId) {
        log.info("Releasing reservation: {}", reservationId);

        StockReservation reservation = stockReservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE reservations can be released. Current status: " + reservation.getStatus());
        }

        Stock stock = stockRepository.findByProductId(reservation.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Stock not found for product: " + reservation.getProductId()));

        int previousQuantity = stock.getAvailableQuantity();

        // Rilascia prenotazione
        stock.release(reservation.getQuantity());
        reservation.release();

        Stock savedStock = stockRepository.save(stock);
        StockReservation savedReservation = stockReservationRepository.save(reservation);
        log.info("Reservation released successfully: {}", reservationId);

        // Crea movimento RELEASE
        createMovement(
            reservation.getProductId(),
            MovementType.RELEASE,
            reservation.getQuantity(),
            previousQuantity,
            savedStock.getAvailableQuantity(),
            ReferenceType.ORDER,
            reservation.getOrderId(),
            "Reservation released - " + reservationId,
            null
        );

        // Gestisci alert scorte basse
        handleLowStockAlert(savedStock);

        return convertToReservationResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByOrderId(Long orderId) {
        log.info("Getting reservations for order: {}", orderId);

        return stockReservationRepository.findByOrderId(orderId).stream()
                .map(this::convertToReservationResponse)
                .collect(Collectors.toList());
    }

    // ========== ADMIN ==========

    @Override
    public StockResponse createStock(CreateStockRequest request) {
        log.info("Creating stock for product: {}", request.getProductId());

        // Verifica che il prodotto esista
        productServiceClient.validateProduct(request.getProductId());

        // Verifica che non esista già stock
        if (stockRepository.existsByProductId(request.getProductId())) {
            throw new DuplicateResourceException("Stock already exists for product: " + request.getProductId());
        }

        // Crea stock
        Stock stock = Stock.builder()
                .productId(request.getProductId())
                .availableQuantity(request.getInitialQuantity())
                .reservedQuantity(0)
                .physicalQuantity(request.getInitialQuantity())
                .minimumQuantity(request.getMinimumQuantity())
                .build();

        Stock savedStock = stockRepository.save(stock);
        log.info("Stock created successfully for product: {}", request.getProductId());

        // Se quantità iniziale > 0, crea movimento IN
        if (request.getInitialQuantity() > 0) {
            createMovement(
                request.getProductId(),
                MovementType.IN,
                request.getInitialQuantity(),
                0,
                request.getInitialQuantity(),
                ReferenceType.MANUAL,
                null,
                "Initial stock creation",
                null
            );
        }

        return convertToStockResponse(savedStock);
    }

    @Override
    public StockResponse updateMinimumQuantity(Long productId, UpdateMinimumQuantityRequest request) {
        log.info("Updating minimum quantity for product {}: {}", productId, request.getMinimumQuantity());

        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product: " + productId));

        stock.setMinimumQuantity(request.getMinimumQuantity());
        Stock savedStock = stockRepository.save(stock);
        log.info("Minimum quantity updated successfully for product: {}", productId);

        // Gestisci alert scorte basse
        handleLowStockAlert(savedStock);

        return convertToStockResponse(savedStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockAlertResponse> getActiveLowStockAlerts() {
        log.info("Getting active low stock alerts");

        return lowStockAlertRepository.findByAlertStatusOrderByCreatedAtDesc(AlertStatus.ACTIVE).stream()
                .map(this::convertToAlertResponse)
                .collect(Collectors.toList());
    }

    // ========== HELPER METHODS ==========

    /**
     * Crea un movimento di stock
     */
    private void createMovement(Long productId, MovementType movementType, Integer quantity,
                                Integer previousQuantity, Integer newQuantity,
                                ReferenceType referenceType, Long referenceId,
                                String notes, Long userId) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .movementType(movementType)
                .quantity(quantity)
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .notes(notes)
                .createdByUserId(userId)
                .build();

        stockMovementRepository.save(movement);
        log.debug("Movement created: type={}, quantity={}", movementType, quantity);
    }

    /**
     * Gestisce alert scorte basse
     */
    private void handleLowStockAlert(Stock stock) {
        boolean isLowStock = stock.isLowStock();

        // Cerca alert attivo esistente
        var existingAlert = lowStockAlertRepository.findByProductIdAndAlertStatus(
            stock.getProductId(), AlertStatus.ACTIVE);

        if (isLowStock && existingAlert.isEmpty()) {
            // Crea nuovo alert
            LowStockAlert alert = LowStockAlert.builder()
                    .productId(stock.getProductId())
                    .availableQuantity(stock.getAvailableQuantity())
                    .minimumQuantity(stock.getMinimumQuantity())
                    .alertStatus(AlertStatus.ACTIVE)
                    .build();

            lowStockAlertRepository.save(alert);
            log.warn("Low stock alert created for product: {}", stock.getProductId());

        } else if (!isLowStock && existingAlert.isPresent()) {
            // Risolvi alert
            LowStockAlert alert = existingAlert.get();
            alert.resolve();
            lowStockAlertRepository.save(alert);
            log.info("Low stock alert resolved for product: {}", stock.getProductId());
        }
    }

    /**
     * Converte Stock entity in StockResponse
     */
    private StockResponse convertToStockResponse(Stock stock) {
        String productName = null;
        try {
            ProductInfo productInfo = productServiceClient.getProduct(stock.getProductId());
            productName = productInfo.getName();
        } catch (Exception e) {
            log.warn("Could not fetch product name for product {}: {}", stock.getProductId(), e.getMessage());
        }

        return StockResponse.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .productName(productName)
                .availableQuantity(stock.getAvailableQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .physicalQuantity(stock.getPhysicalQuantity())
                .minimumQuantity(stock.getMinimumQuantity())
                .isLowStock(stock.isLowStock())
                .createdAt(stock.getCreatedAt())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    /**
     * Converte StockMovement entity in StockMovementResponse
     */
    private StockMovementResponse convertToMovementResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .productId(movement.getProductId())
                .movementType(movement.getMovementType())
                .quantity(movement.getQuantity())
                .previousQuantity(movement.getPreviousQuantity())
                .newQuantity(movement.getNewQuantity())
                .referenceType(movement.getReferenceType())
                .referenceId(movement.getReferenceId())
                .notes(movement.getNotes())
                .createdByUserId(movement.getCreatedByUserId())
                .createdAt(movement.getCreatedAt())
                .build();
    }

    /**
     * Converte StockReservation entity in ReservationResponse
     */
    private ReservationResponse convertToReservationResponse(StockReservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .productId(reservation.getProductId())
                .orderId(reservation.getOrderId())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .expiresAt(reservation.getExpiresAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .confirmedAt(reservation.getConfirmedAt())
                .releasedAt(reservation.getReleasedAt())
                .build();
    }

    /**
     * Converte LowStockAlert entity in LowStockAlertResponse
     */
    private LowStockAlertResponse convertToAlertResponse(LowStockAlert alert) {
        String productName = null;
        try {
            ProductInfo productInfo = productServiceClient.getProduct(alert.getProductId());
            productName = productInfo.getName();
        } catch (Exception e) {
            log.warn("Could not fetch product name for product {}: {}", alert.getProductId(), e.getMessage());
        }

        return LowStockAlertResponse.builder()
                .id(alert.getId())
                .productId(alert.getProductId())
                .productName(productName)
                .availableQuantity(alert.getAvailableQuantity())
                .minimumQuantity(alert.getMinimumQuantity())
                .alertStatus(alert.getAlertStatus())
                .createdAt(alert.getCreatedAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }
}
