package com.retailsports.payment_service.repository;

import com.retailsports.payment_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ========== QUERY BASE ==========

    // Trova tutti gli item di un ordine
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    // Trova item per product ID
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);

    // Conta item in un ordine
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.id = :orderId")
    long countByOrderId(@Param("orderId") Long orderId);

    // ========== QUERY PER STATISTICHE ==========

    // Prodotti più venduti (top N)
    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProducts();

    // Calcola totale quantità venduta per prodotto
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Long getTotalQuantitySoldByProductId(@Param("productId") Long productId);

    // Calcola revenue totale per prodotto
    @Query("SELECT SUM(oi.totalCents) FROM OrderItem oi WHERE oi.productId = :productId")
    Long getTotalRevenueByProductId(@Param("productId") Long productId);
}
