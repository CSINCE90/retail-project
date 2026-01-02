package com.retailsports.stock_service.scheduler;

import com.retailsports.stock_service.entity.Stock;
import com.retailsports.stock_service.entity.StockMovement;
import com.retailsports.stock_service.entity.StockMovement.MovementType;
import com.retailsports.stock_service.entity.StockMovement.ReferenceType;
import com.retailsports.stock_service.entity.StockReservation;
import com.retailsports.stock_service.repository.StockMovementRepository;
import com.retailsports.stock_service.repository.StockRepository;
import com.retailsports.stock_service.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler per gestire la scadenza delle prenotazioni stock
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationScheduler {

    private final StockReservationRepository stockReservationRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    /**
     * Job schedulato per la scadenza delle prenotazioni
     * Eseguito ogni X minuti (configurabile in application.yaml)
     */
    @Scheduled(
        fixedDelayString = "${stock.reservation.cleanup-interval-minutes:5}",
        initialDelay = 60000,
        timeUnit = TimeUnit.MINUTES
    )
    @Transactional
    public void expireReservations() {
        log.info("Starting reservation expiration job");

        try {
            // Trova tutte le prenotazioni scadute
            List<StockReservation> expiredReservations = 
                stockReservationRepository.findExpiredReservations(LocalDateTime.now());

            if (expiredReservations.isEmpty()) {
                log.info("No expired reservations found");
                return;
            }

            log.info("Found {} expired reservations to process", expiredReservations.size());

            int processedCount = 0;
            for (StockReservation reservation : expiredReservations) {
                try {
                    processExpiredReservation(reservation);
                    processedCount++;
                } catch (Exception e) {
                    log.error("Error processing expired reservation {}: {}", 
                        reservation.getId(), e.getMessage(), e);
                }
            }

            log.info("Reservation expiration job completed. Processed: {}/{}", 
                processedCount, expiredReservations.size());

        } catch (Exception e) {
            log.error("Error in reservation expiration job: {}", e.getMessage(), e);
        }
    }

    /**
     * Processa una singola prenotazione scaduta
     */
    private void processExpiredReservation(StockReservation reservation) {
        log.info("Processing expired reservation: {} for order {}", 
            reservation.getId(), reservation.getOrderId());

        // Ottieni lo stock
        Stock stock = stockRepository.findByProductId(reservation.getProductId())
                .orElseThrow(() -> new RuntimeException(
                    "Stock not found for product: " + reservation.getProductId()));

        int previousQuantity = stock.getAvailableQuantity();

        // Rilascia lo stock (reserved → available)
        stock.release(reservation.getQuantity());

        // Marca la prenotazione come scaduta
        reservation.expire();

        // Salva le modifiche
        stockRepository.save(stock);
        stockReservationRepository.save(reservation);

        // Crea movimento RELEASE
        StockMovement movement = StockMovement.builder()
                .productId(reservation.getProductId())
                .movementType(MovementType.RELEASE)
                .quantity(reservation.getQuantity())
                .previousQuantity(previousQuantity)
                .newQuantity(stock.getAvailableQuantity())
                .referenceType(ReferenceType.ORDER)
                .referenceId(reservation.getOrderId())
                .notes("Reservation expired - " + reservation.getId())
                .build();

        stockMovementRepository.save(movement);

        log.info("Expired reservation processed successfully: {}", reservation.getId());
    }
}
