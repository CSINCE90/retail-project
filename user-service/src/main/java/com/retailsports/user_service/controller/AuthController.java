package com.retailsports.user_service.controller;

import com.retailsports.user_service.dto.request.LoginRequest;
import com.retailsports.user_service.dto.request.RefreshTokenRequest;
import com.retailsports.user_service.dto.request.RegisterRequest;
import com.retailsports.user_service.dto.response.ApiResponse;
import com.retailsports.user_service.dto.response.AuthResponse;
import com.retailsports.user_service.dto.response.UserResponse;
import com.retailsports.user_service.security.SecurityUtils;
import com.retailsports.user_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Registrazione nuovo utente
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        log.info("Registration request for username: {}", request.getUsername());
        
        UserResponse userResponse = authService.register(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully. Please verify your email.", userResponse));
    }

    /**
     * POST /api/auth/login
     * Login utente
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Login request for username: {}", request.getUsername());
        
        // Estrai IP e User-Agent dalla richiesta
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        AuthResponse authResponse = authService.login(request, ipAddress, userAgent);
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    /**
     * POST /api/auth/refresh
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        
        log.info("Refresh token request");
        
        AuthResponse authResponse = authService.refreshAccessToken(request.getRefreshToken());
        
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

    /**
     * POST /api/auth/logout
     * Logout utente (invalida refresh token)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody(required = false) RefreshTokenRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Logout request for user: {}", userId);
        
        String refreshToken = request != null ? request.getRefreshToken() : null;
        authService.logout(userId, refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

    /**
     * POST /api/auth/logout-all
     * Logout da tutti i dispositivi
     */
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevices() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Logout all devices request for user: {}", userId);
        
        authService.logoutAllDevices(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices", null));
    }

    // ====================================
    // UTILITY METHODS
    // ====================================

    /**
     * Estrae IP del client dalla richiesta
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}