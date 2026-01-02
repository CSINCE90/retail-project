package com.retailsports.stock_service.repository;

import com.retailsports.stock_service.entity.StockReservation;
import com.retailsports.stock_service.entity.StockReservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository per l'entità StockReservation
 */
@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    /**
     * Trova prenotazioni per ordine
     */
    List<StockReservation> findByOrderId(Long orderId);

    /**
     * Trova prenotazioni attive per un prodotto
     */
    List<StockReservation> findByProductIdAndStatus(Long productId, ReservationStatus status);

    /**
     * Trova prenotazioni scadute ma ancora attive
     */
    @Query("SELECT r FROM StockReservation r WHERE r.status = 'ACTIVE' AND r.expiresAt < :currentTime")
    List<StockReservation> findExpiredReservations(LocalDateTime currentTime);

    /**
     * Trova prenotazione attiva per ordine e prodotto
     */
    Optional<StockReservation> findByOrderIdAndProductIdAndStatus(
        Long orderId, 
        Long productId, 
        ReservationStatus status
    );

    /**
     * Conta prenotazioni attive per un prodotto
     */
    long countByProductIdAndStatus(Long productId, ReservationStatus status);
}
