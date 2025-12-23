package com.retailsports.user_service.controller;

import com.retailsports.user_service.dto.request.UpdateUserRequest;
import com.retailsports.user_service.dto.response.ApiResponse;
import com.retailsports.user_service.dto.response.UserResponse;
import com.retailsports.user_service.security.SecurityUtils;
import com.retailsports.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me
     * Ottieni profilo utente corrente
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Get current user profile: {}", userId);
        
        UserResponse userResponse = userService.getCurrentUserProfile(userId);
        
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * PUT /api/users/me
     * Aggiorna profilo utente corrente
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request) {
        
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Update current user profile: {}", userId);
        
        UserResponse userResponse = userService.updateUserProfile(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userResponse));
    }

    /**
     * DELETE /api/users/me
     * Elimina account utente corrente (soft delete)
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("Delete current user account: {}", userId);
        
        userService.softDeleteUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }

    /**
     * GET /api/users
     * Lista tutti gli utenti con paginazione (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.info("Get all users - page: {}, size: {}, sortBy: {}, direction: {}", 
                 page, size, sortBy, direction);
        
        Page<UserResponse> users = userService.getAllUsers(page, size, sortBy, direction);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * GET /api/users/search
     * Cerca utenti per nome/cognome con paginazione (ADMIN only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Search users by name: {} - page: {}, size: {}", query, page, size);
        
        Page<UserResponse> users = userService.searchUsersByName(query, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * GET /api/users/role/{roleName}
     * Ottieni utenti per ruolo con paginazione (ADMIN only)
     */
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsersByRole(
            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Get users by role: {} - page: {}, size: {}", roleName, page, size);
        
        Page<UserResponse> users = userService.getUsersByRole(roleName, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * GET /api/users/{id}
     * Ottieni dettagli utente per ID (ADMIN only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Get user by id: {}", id);
        
        UserResponse userResponse = userService.getCurrentUserProfile(id);
        
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * GET /api/users/statistics
     * Ottieni statistiche utenti (ADMIN only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserService.UserStatistics>> getUserStatistics() {
        log.info("Get user statistics");
        
        UserService.UserStatistics stats = userService.getUserStatistics();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}