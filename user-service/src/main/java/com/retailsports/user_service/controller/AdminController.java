package com.retailsports.user_service.controller;

import com.retailsports.user_service.dto.response.ApiResponse;
import com.retailsports.user_service.service.LoginAuditService;
import com.retailsports.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.retailsports.user_service.model.LoginAudit;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final LoginAuditService loginAuditService;

    /**
     * PUT /api/admin/users/{id}/toggle-status
     * Abilita/Disabilita utente
     */
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        
        log.info("Toggle user status: {} - enabled: {}", id, enabled);
        
        userService.toggleUserStatus(id, enabled);
        
        String message = enabled ? "User enabled successfully" : "User disabled successfully";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    /**
     * POST /api/admin/users/{id}/restore
     * Ripristina utente soft deleted
     */
    @PostMapping("/users/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreUser(@PathVariable Long id) {
        log.info("Restore user: {}", id);
        
        userService.restoreUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User restored successfully", null));
    }

    /**
     * DELETE /api/admin/users/{id}
     * Soft delete utente
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Soft delete user: {}", id);
        
        userService.softDeleteUser(id);
        
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    /**
     * GET /api/admin/audit/user/{userId}
     * Ottieni storico login di un utente
     */
    @GetMapping("/audit/user/{userId}")
    public ResponseEntity<ApiResponse<Page<LoginAudit>>> getUserLoginHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Get login history for user: {} - page: {}, size: {}", userId, page, size);
        
        Page<LoginAudit> history = loginAuditService.getUserLoginHistory(userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * GET /api/admin/audit/statistics/daily
     * Statistiche login giornaliere
     */
    @GetMapping("/audit/statistics/daily")
    public ResponseEntity<ApiResponse<LoginAuditService.LoginStatistics>> getDailyLoginStatistics() {
        log.info("Get daily login statistics");
        
        LoginAuditService.LoginStatistics stats = loginAuditService.getDailyLoginStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * GET /api/admin/audit/statistics/weekly
     * Statistiche login settimanali
     */
    @GetMapping("/audit/statistics/weekly")
    public ResponseEntity<ApiResponse<LoginAuditService.LoginStatistics>> getWeeklyLoginStatistics() {
        log.info("Get weekly login statistics");
        
        LoginAuditService.LoginStatistics stats = loginAuditService.getWeeklyLoginStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * GET /api/admin/audit/statistics/monthly
     * Statistiche login mensili
     */
    @GetMapping("/audit/statistics/monthly")
    public ResponseEntity<ApiResponse<LoginAuditService.LoginStatistics>> getMonthlyLoginStatistics() {
        log.info("Get monthly login statistics");
        
        LoginAuditService.LoginStatistics stats = loginAuditService.getMonthlyLoginStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}