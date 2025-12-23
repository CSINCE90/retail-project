package com.retailsports.user_service.repository;

import com.retailsports.user_service.model.VerificationToken;
import com.retailsports.user_service.model.VerificationToken.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    // Trova token per token string
    Optional<VerificationToken> findByToken(String token);

    // Trova token per utente e tipo
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.user.id = :userId AND vt.type = :type AND vt.used = false")
    List<VerificationToken> findByUserIdAndTypeAndNotUsed(@Param("userId") Long userId, @Param("type") TokenType type);

    // Trova token valido (non scaduto e non usato)
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.token = :token " +
           "AND vt.used = false AND vt.expiresAt > :now")
    Optional<VerificationToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // Elimina tutti i token di un utente
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // Elimina tutti i token scaduti o usati
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiresAt < :now OR vt.used = true")
    int deleteExpiredOrUsedTokens(@Param("now") LocalDateTime now);

    // Marca come usato un token specifico
    @Modifying
    @Query("UPDATE VerificationToken vt SET vt.used = true, vt.usedAt = :usedAt WHERE vt.token = :token")
    void markTokenAsUsed(@Param("token") String token, @Param("usedAt") LocalDateTime usedAt);
}
