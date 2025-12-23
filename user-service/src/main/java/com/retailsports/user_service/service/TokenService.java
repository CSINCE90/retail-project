package com.retailsports.user_service.service;

import com.retailsports.user_service.exception.UnauthorizedException;
import com.retailsports.user_service.model.RefreshToken;
import com.retailsports.user_service.model.User;
import com.retailsports.user_service.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Key jwtSecret;
    private final long jwtExpirationMs;
    private final long refreshExpirationMs;

    public TokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpirationMs,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * Genera JWT Access Token
     */
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("username", user.getUsername())
            .claim("email", user.getEmail())
            .claim("roles", user.getRoles().stream()
                .map(role -> role.getName())
                .toList())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtSecret, SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Valida JWT Token
     */
    public Claims validateAccessToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new UnauthorizedException("Invalid or expired token");
        }
    }

    /**
     * Estrai User ID dal token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = validateAccessToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Crea Refresh Token
     */
    public RefreshToken createRefreshToken(User user) {
        // Genera token UUID
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(token)
            .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
            .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valida Refresh Token
     */
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        return refreshToken;
    }

    /**
     * Elimina Refresh Token
     */
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    /**
     * Elimina tutti i Refresh Token di un utente
     */
    public void deleteAllRefreshTokensForUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Pulizia token scaduti (scheduled task)
     */
    public int cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up {} expired refresh tokens", deleted);
        return deleted;
    }
}