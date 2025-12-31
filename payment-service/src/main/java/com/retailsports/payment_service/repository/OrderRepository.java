package com.retailsports.payment_service.repository;

import com.retailsports.payment_service.entity.Order;
import com.retailsports.payment_service.enums.OrderStatus;
import com.retailsports.payment_service.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ========== QUERY BASE ==========

    // Trova ordine per numero ordine
    @Query("SELECT o FROM Order o WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);

    // Verifica esistenza per numero ordine
    boolean existsByOrderNumber(String orderNumber);

    // ========== QUERY PER UTENTE ==========

    // Trova tutti gli ordini di un utente
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") Long userId);

    // Trova ordini di un utente con paginazione
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // Conta ordini per utente
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    // ========== QUERY PER STATO ==========

    // Trova ordini per stato
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByStatus(@Param("status") OrderStatus status);

    // Trova ordini per stato con paginazione
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);

    // Trova ordini per stato pagamento
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = :paymentStatus ORDER BY o.createdAt DESC")
    List<Order> findByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);

    // Trova ordini per utente e stato
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status ORDER BY o.createdAt DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

    // Conta ordini per stato
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    // ========== QUERY PER DATA ==========

    // Trova ordini per range di date
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Trova ordini per range di date con paginazione
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    Page<Order> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                Pageable pageable);

    // Trova ordini recenti (ultimi N giorni)
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("since") LocalDateTime since);

    // ========== QUERY COMPLESSE CON FILTRI ==========

    // Trova ordini con filtri multipli
    @Query("SELECT o FROM Order o WHERE " +
           "(:userId IS NULL OR o.userId = :userId) " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) " +
           "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findWithFilters(
        @Param("userId") Long userId,
        @Param("status") OrderStatus status,
        @Param("paymentStatus") PaymentStatus paymentStatus,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    // ========== QUERY PER TRACKING ==========

    // Trova ordine per tracking number
    @Query("SELECT o FROM Order o WHERE o.trackingNumber = :trackingNumber")
    Optional<Order> findByTrackingNumber(@Param("trackingNumber") String trackingNumber);

    // ========== QUERY PER STATISTICHE ==========

    // Calcola totale vendite per utente
    @Query("SELECT SUM(o.totalCents) FROM Order o WHERE o.userId = :userId AND o.paymentStatus = 'COMPLETED'")
    Long getTotalSalesByUserId(@Param("userId") Long userId);

    // Calcola totale vendite per periodo
    @Query("SELECT SUM(o.totalCents) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.paymentStatus = 'COMPLETED'")
    Long getTotalSalesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Conta ordini completati per periodo
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status = 'DELIVERED'")
    long countCompletedOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Media valore ordini
    @Query("SELECT AVG(o.totalCents) FROM Order o WHERE o.paymentStatus = 'COMPLETED'")
    Double getAverageOrderValue();

    // ========== QUERY PER ORDINI DA PROCESSARE ==========

    // Trova ordini pending che necessitano attenzione
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :threshold ORDER BY o.createdAt ASC")
    List<Order> findPendingOrdersOlderThan(@Param("threshold") LocalDateTime threshold);

    // Trova ordini confermati ma non ancora spediti
    @Query("SELECT o FROM Order o WHERE o.status = 'CONFIRMED' AND o.shippedAt IS NULL ORDER BY o.confirmedAt ASC")
    List<Order> findConfirmedOrdersNotShipped();

    // Trova ordini spediti ma non ancora consegnati
    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' AND o.deliveredAt IS NULL ORDER BY o.shippedAt ASC")
    List<Order> findShippedOrdersNotDelivered();
}
