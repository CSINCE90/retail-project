package com.retailsports.stock_service.repository;

import com.retailsports.stock_service.entity.StockMovement;
import com.retailsports.stock_service.entity.StockMovement.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository per l'entità StockMovement
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    /**
     * Trova movimenti per productId con paginazione
     */
    Page<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    /**
     * Trova movimenti per productId senza paginazione
     */
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
     * Trova movimenti per tipo
     */
    List<StockMovement> findByMovementTypeOrderByCreatedAtDesc(MovementType movementType);

    /**
     * Trova movimenti in un range di date
     */
    List<StockMovement> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );

    /**
     * Trova movimenti per productId e tipo
     */
    List<StockMovement> findByProductIdAndMovementTypeOrderByCreatedAtDesc(
        Long productId, 
        MovementType movementType
    );
}
