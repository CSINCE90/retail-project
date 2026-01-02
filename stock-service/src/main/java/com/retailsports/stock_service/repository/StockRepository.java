package com.retailsports.stock_service.repository;

import com.retailsports.stock_service.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'entità Stock
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Trova stock per productId
     */
    Optional<Stock> findByProductId(Long productId);

    /**
     * Trova tutti i prodotti con scorte basse (available < minimum)
     */
    @Query("SELECT s FROM Stock s WHERE s.availableQuantity < s.minimumQuantity")
    List<Stock> findLowStockProducts();

    /**
     * Trova tutti i prodotti con quantità disponibile minore di una soglia
     */
    List<Stock> findByAvailableQuantityLessThan(Integer threshold);

    /**
     * Verifica se esiste stock per un prodotto
     */
    boolean existsByProductId(Long productId);
}
