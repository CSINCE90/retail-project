package com.retailsports.user_service.service;

import com.retailsports.user_service.dto.request.UpdateUserRequest;
import com.retailsports.user_service.dto.response.UserResponse;
import com.retailsports.user_service.exception.ResourceNotFoundException;
import com.retailsports.user_service.exception.BadRequestException;
import com.retailsports.user_service.model.User;
import com.retailsports.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Trova utente per ID (solo attivi)
     */
    public User findById(Long id) {
        return userRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Trova utente per username (solo attivi)
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Trova utente per email (solo attivi)
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Ottieni tutti gli utenti attivi con paginazione e ordinamento
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<User> users = userRepository.findAll(pageable);
        
        return users.map(this::convertToUserResponse);
    }

    /**
     * Cerca utenti per nome/cognome con paginazione
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsersByName(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "firstName"));
        
        List<User> users = userRepository.searchByName(search);
        
        // Converti in Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        
        List<UserResponse> userResponses = users.subList(start, end)
            .stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            userResponses, 
            pageable, 
            users.size()
        );
    }

    /**
     * Trova utenti per ruolo con paginazione
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(String roleName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "username"));
        
        List<User> users = userRepository.findByRoleName(roleName);
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        
        List<UserResponse> userResponses = users.subList(start, end)
            .stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            userResponses,
            pageable,
            users.size()
        );
    }

    /**
     * Ottieni profilo utente corrente
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(Long userId) {
        User user = findById(userId);
        return convertToUserResponse(user);
    }

    /**
     * Aggiorna profilo utente
     */
    public UserResponse updateUserProfile(Long userId, UpdateUserRequest request) {
        User user = findById(userId);

        // Verifica se email è già in uso da un altro utente
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            user.setEmail(request.getEmail());
            user.setEmailVerified(false); // Richiede nuova verifica
        }

        // Aggiorna altri campi
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", userId);

        return convertToUserResponse(updatedUser);
    }

    /**
     * Abilita/Disabilita utente (solo ADMIN)
     */
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = findById(userId);
        user.setEnabled(enabled);
        userRepository.save(user);
        
        log.info("User {} status changed to: {}", userId, enabled ? "enabled" : "disabled");
    }

    /**
     * Soft delete utente
     */
    public void softDeleteUser(Long userId) {
        User user = findById(userId);
        user.softDelete();
        userRepository.save(user);
        
        log.info("User soft deleted: {}", userId);
    }

    /**
     * Ripristina utente soft deleted (solo ADMIN)
     */
    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!user.isDeleted()) {
            throw new BadRequestException("User is not deleted");
        }
        
        user.restore();
        userRepository.save(user);
        
        log.info("User restored: {}", userId);
    }

    /**
     * Verifica email utente
     */
    public void verifyUserEmail(Long userId) {
        User user = findById(userId);
        user.setEmailVerified(true);
        userRepository.save(user);
        
        log.info("User email verified: {}", userId);
    }

    /**
     * Conta utenti totali (solo attivi)
     */
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.findAllActiveUsers().size();
    }

    /**
     * Conta utenti per ruolo
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(String roleName) {
        return userRepository.findByRoleName(roleName).size();
    }

    /**
     * Ottieni statistiche utenti
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        long totalActive = countActiveUsers();
        long totalDeleted = userRepository.findAllDeletedUsers().size();
        long unverifiedEmails = userRepository.findUsersWithUnverifiedEmail().size();
        long disabledUsers = userRepository.findDisabledUsers().size();
        
        return UserStatistics.builder()
            .totalActive(totalActive)
            .totalDeleted(totalDeleted)
            .unverifiedEmails(unverifiedEmails)
            .disabledUsers(disabledUsers)
            .build();
    }

    // ====================================
    // UTILITY METHODS
    // ====================================

    /**
     * Converte User entity in UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .enabled(user.getEnabled())
            .emailVerified(user.getEmailVerified())
            .roles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()))
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    // Inner class per statistiche
    @lombok.Data
    @lombok.Builder
    public static class UserStatistics {
        private long totalActive;
        private long totalDeleted;
        private long unverifiedEmails;
        private long disabledUsers;
    }
}
