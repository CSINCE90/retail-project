package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.Discount;
import com.retailsports.product_service.model.Discount.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    // Trova sconto per codice
    @Query("SELECT d FROM Discount d WHERE d.code = :code")
    Optional<Discount> findByCode(@Param("code") String code);

    // Trova sconto attivo per codice
    @Query("SELECT d FROM Discount d WHERE d.code = :code AND d.isActive = true")
    Optional<Discount> findActiveByCode(@Param("code") String code);

    // Trova tutti gli sconti attivi
    @Query("SELECT d FROM Discount d WHERE d.isActive = true ORDER BY d.createdAt DESC")
    List<Discount> findAllActive();

    // Trova sconti validi al momento (attivi e nel periodo di validità)
    @Query("SELECT d FROM Discount d WHERE d.isActive = true " +
           "AND d.startsAt <= :now AND d.endsAt >= :now " +
           "AND (d.maxUses IS NULL OR d.currentUses < d.maxUses) " +
           "ORDER BY d.createdAt DESC")
    List<Discount> findValidDiscounts(@Param("now") LocalDateTime now);

    // Trova sconti per tipo
    @Query("SELECT d FROM Discount d WHERE d.type = :type ORDER BY d.createdAt DESC")
    List<Discount> findByType(@Param("type") DiscountType type);

    // Trova sconti per prodotto
    @Query("SELECT d FROM Discount d JOIN d.products p WHERE p.id = :productId ORDER BY d.createdAt DESC")
    List<Discount> findByProductId(@Param("productId") Long productId);

    // Trova sconti validi per prodotto
    @Query("SELECT d FROM Discount d JOIN d.products p WHERE p.id = :productId " +
           "AND d.isActive = true AND d.startsAt <= :now AND d.endsAt >= :now " +
           "AND (d.maxUses IS NULL OR d.currentUses < d.maxUses)")
    List<Discount> findValidDiscountsByProductId(@Param("productId") Long productId, @Param("now") LocalDateTime now);

    // Trova sconti scaduti
    @Query("SELECT d FROM Discount d WHERE d.endsAt < :now ORDER BY d.endsAt DESC")
    List<Discount> findExpiredDiscounts(@Param("now") LocalDateTime now);

    // Trova sconti che stanno per scadere (entro X giorni)
    @Query("SELECT d FROM Discount d WHERE d.isActive = true " +
           "AND d.endsAt BETWEEN :now AND :expiryDate ORDER BY d.endsAt ASC")
    List<Discount> findExpiringDiscounts(@Param("now") LocalDateTime now, @Param("expiryDate") LocalDateTime expiryDate);

    // Trova sconti che hanno raggiunto il limite di utilizzi
    @Query("SELECT d FROM Discount d WHERE d.maxUses IS NOT NULL AND d.currentUses >= d.maxUses")
    List<Discount> findFullyUsedDiscounts();

    // Verifica se codice esiste
    boolean existsByCode(String code);

    // Conta prodotti con uno sconto specifico
    @Query("SELECT COUNT(p) FROM Discount d JOIN d.products p WHERE d.id = :discountId")
    long countProductsByDiscountId(@Param("discountId") Long discountId);

    // Disattiva sconti scaduti
    @Modifying
    @Query("UPDATE Discount d SET d.isActive = false WHERE d.endsAt < :now AND d.isActive = true")
    int deactivateExpiredDiscounts(@Param("now") LocalDateTime now);

    // Incrementa currentUses di uno sconto
    @Modifying
    @Query("UPDATE Discount d SET d.currentUses = d.currentUses + 1 WHERE d.id = :discountId")
    void incrementUsage(@Param("discountId") Long discountId);

    // Trova sconti per importo minimo
    @Query("SELECT d FROM Discount d WHERE d.isActive = true " +
           "AND (d.minPurchaseAmountCents IS NULL OR d.minPurchaseAmountCents <= :purchaseAmount) " +
           "AND d.startsAt <= :now AND d.endsAt >= :now " +
           "AND (d.maxUses IS NULL OR d.currentUses < d.maxUses)")
    List<Discount> findApplicableDiscounts(@Param("purchaseAmount") Integer purchaseAmount, @Param("now") LocalDateTime now);
}
