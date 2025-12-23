package com.retailsports.user_service.repository;

import com.retailsports.user_service.model.LoginAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {

    // Trova audit log per utente (paginato)
    @Query("SELECT la FROM LoginAudit la WHERE la.user.id = :userId ORDER BY la.createdAt DESC")
    Page<LoginAudit> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // Trova audit log per username (anche tentativi falliti)
    @Query("SELECT la FROM LoginAudit la WHERE la.username = :username ORDER BY la.createdAt DESC")
    Page<LoginAudit> findByUsername(@Param("username") String username, Pageable pageable);

    // Conta tentativi falliti recenti per username (ultime N ore)
    @Query("SELECT COUNT(la) FROM LoginAudit la WHERE la.username = :username " +
           "AND la.success = false AND la.createdAt > :since")
    long countFailedAttemptsSince(@Param("username") String username, @Param("since") LocalDateTime since);

    // Trova tentativi falliti per IP
    @Query("SELECT la FROM LoginAudit la WHERE la.ipAddress = :ipAddress AND la.success = false " +
           "ORDER BY la.createdAt DESC")
    List<LoginAudit> findFailedAttemptsByIp(@Param("ipAddress") String ipAddress);

    // Trova login riusciti per utente
    @Query("SELECT la FROM LoginAudit la WHERE la.user.id = :userId AND la.success = true " +
           "ORDER BY la.createdAt DESC")
    Page<LoginAudit> findSuccessfulLoginsByUserId(@Param("userId") Long userId, Pageable pageable);

    // Ultimo login riuscito per utente
    @Query("SELECT la FROM LoginAudit la WHERE la.user.id = :userId AND la.success = true " +
           "ORDER BY la.createdAt DESC")
    List<LoginAudit> findLastSuccessfulLogin(@Param("userId") Long userId, Pageable pageable);

    // Statistiche login per periodo
    @Query("SELECT la.success, COUNT(la) FROM LoginAudit la " +
           "WHERE la.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY la.success")
    List<Object[]> getLoginStatisticsBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
}
