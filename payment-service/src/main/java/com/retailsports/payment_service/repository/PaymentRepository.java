package com.retailsports.payment_service.repository;

import com.retailsports.payment_service.entity.Payment;
import com.retailsports.payment_service.enums.PaymentMethod;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // ========== QUERY BASE ==========

    // Trova pagamenti per ordine
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.createdAt DESC")
    List<Payment> findByOrderId(@Param("orderId") Long orderId);

    // Trova ultimo pagamento per ordine
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.createdAt DESC")
    Optional<Payment> findLatestByOrderId(@Param("orderId") Long orderId);

    // Trova pagamento per transaction ID
    @Query("SELECT p FROM Payment p WHERE p.transactionId = :transactionId")
    Optional<Payment> findByTransactionId(@Param("transactionId") String transactionId);

    // ========== QUERY PER STATO ==========

    // Trova pagamenti per stato
    @Query("SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findByStatus(@Param("status") PaymentStatus status);

    // Trova pagamenti per stato con paginazione
    @Query("SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    Page<Payment> findByStatus(@Param("status") PaymentStatus status, Pageable pageable);

    // Conta pagamenti per stato
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);

    // ========== QUERY PER METODO PAGAMENTO ==========

    // Trova pagamenti per metodo
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod ORDER BY p.createdAt DESC")
    List<Payment> findByPaymentMethod(@Param("paymentMethod") PaymentMethod paymentMethod);

    // Conta pagamenti per metodo
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = :paymentMethod")
    long countByPaymentMethod(@Param("paymentMethod") PaymentMethod paymentMethod);

    // ========== QUERY PER DATA ==========

    // Trova pagamenti per range di date
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Trova pagamenti completati per range di date
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND p.completedAt BETWEEN :startDate AND :endDate ORDER BY p.completedAt DESC")
    List<Payment> findCompletedByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== QUERY COMPLESSE CON FILTRI ==========

    // Trova pagamenti con filtri multipli
    @Query("SELECT p FROM Payment p WHERE " +
           "(:status IS NULL OR p.status = :status) " +
           "AND (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) " +
           "AND (:startDate IS NULL OR p.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR p.createdAt <= :endDate) " +
           "ORDER BY p.createdAt DESC")
    Page<Payment> findWithFilters(
        @Param("status") PaymentStatus status,
        @Param("paymentMethod") PaymentMethod paymentMethod,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    // ========== QUERY PER STATISTICHE ==========

    // Calcola totale pagamenti completati
    @Query("SELECT SUM(p.amountCents) FROM Payment p WHERE p.status = 'COMPLETED'")
    Long getTotalCompletedPayments();

    // Calcola totale pagamenti per periodo
    @Query("SELECT SUM(p.amountCents) FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND p.completedAt BETWEEN :startDate AND :endDate")
    Long getTotalPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Calcola totale pagamenti per metodo
    @Query("SELECT SUM(p.amountCents) FROM Payment p WHERE p.paymentMethod = :paymentMethod " +
           "AND p.status = 'COMPLETED'")
    Long getTotalPaymentsByMethod(@Param("paymentMethod") PaymentMethod paymentMethod);

    // Media valore pagamenti
    @Query("SELECT AVG(p.amountCents) FROM Payment p WHERE p.status = 'COMPLETED'")
    Double getAveragePaymentValue();

    // ========== QUERY PER FAILED PAYMENTS ==========

    // Trova pagamenti falliti recenti
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' " +
           "AND p.failedAt >= :since ORDER BY p.failedAt DESC")
    List<Payment> findRecentFailedPayments(@Param("since") LocalDateTime since);

    // Conta pagamenti falliti per ordine
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.order.id = :orderId AND p.status = 'FAILED'")
    long countFailedPaymentsByOrderId(@Param("orderId") Long orderId);

    // ========== VERIFICHE ESISTENZA ==========

    // Verifica se esiste pagamento completato per ordine
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.order.id = :orderId AND p.status = 'COMPLETED'")
    boolean hasCompletedPayment(@Param("orderId") Long orderId);
}
