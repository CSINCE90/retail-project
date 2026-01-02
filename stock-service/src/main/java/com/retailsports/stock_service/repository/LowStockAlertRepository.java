package com.retailsports.stock_service.repository;

import com.retailsports.stock_service.entity.LowStockAlert;
import com.retailsports.stock_service.entity.LowStockAlert.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'entità LowStockAlert
 */
@Repository
public interface LowStockAlertRepository extends JpaRepository<LowStockAlert, Long> {

    /**
     * Trova alert per status
     */
    List<LowStockAlert> findByAlertStatusOrderByCreatedAtDesc(AlertStatus alertStatus);

    /**
     * Trova alert attivi per un prodotto
     */
    Optional<LowStockAlert> findByProductIdAndAlertStatus(Long productId, AlertStatus alertStatus);

    /**
     * Trova tutti gli alert per un prodotto
     */
    List<LowStockAlert> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
     * Conta alert attivi
     */
    long countByAlertStatus(AlertStatus alertStatus);
}
