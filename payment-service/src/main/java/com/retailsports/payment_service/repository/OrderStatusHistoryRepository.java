package com.retailsports.payment_service.repository;

import com.retailsports.payment_service.entity.OrderStatusHistory;
import com.retailsports.payment_service.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    // ========== QUERY BASE ==========

    // Trova storico per ordine
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id = :orderId ORDER BY osh.createdAt DESC")
    List<OrderStatusHistory> findByOrderId(@Param("orderId") Long orderId);

    // Trova storico per ordine ordinato per data
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id = :orderId ORDER BY osh.createdAt ASC")
    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(@Param("orderId") Long orderId);

    // ========== QUERY PER STATO ==========

    // Trova storico per nuovo stato
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.newStatus = :status ORDER BY osh.createdAt DESC")
    List<OrderStatusHistory> findByNewStatus(@Param("status") OrderStatus status);

    // ========== QUERY PER UTENTE ==========

    // Trova modifiche effettuate da un utente
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.changedByUserId = :userId ORDER BY osh.createdAt DESC")
    List<OrderStatusHistory> findByChangedByUserId(@Param("userId") Long userId);

    // Trova modifiche effettuate da admin
    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.changedByAdmin = true ORDER BY osh.createdAt DESC")
    List<OrderStatusHistory> findAdminChanges();

    // ========== QUERY PER STATISTICHE ==========

    // Conta transizioni di stato per un ordine
    @Query("SELECT COUNT(osh) FROM OrderStatusHistory osh WHERE osh.order.id = :orderId")
    long countByOrderId(@Param("orderId") Long orderId);
}
