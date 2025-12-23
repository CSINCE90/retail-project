package com.retailsports.user_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SecurityUtils {

    /**
     * Ottieni ID dell'utente corrente autenticato
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            // Il principal è l'ID dell'utente (Long) settato nel JwtAuthenticationFilter
            if (principal instanceof Long) {
                return (Long) principal;
            }
        }
        
        return null;
    }

    /**
     * Ottieni username dell'utente corrente
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        
        return null;
    }

    /**
     * Verifica se l'utente corrente ha un ruolo specifico
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    /**
     * Verifica se l'utente corrente ha il ruolo ADMIN
     */
    public static boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    /**
     * Verifica se l'utente corrente ha il ruolo USER
     */
    public static boolean isUser() {
        return hasRole("ROLE_USER");
    }

    /**
     * Verifica se l'utente corrente ha il ruolo EMPLOYEE
     */
    public static boolean isEmployee() {
        return hasRole("ROLE_EMPLOYEE");
    }

    /**
     * Verifica se l'utente è autenticato
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
            && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Ottieni tutti i ruoli dell'utente corrente
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            return authentication.getAuthorities();
        }
        
        return null;
    }

    /**
     * Verifica se l'utente corrente è il proprietario della risorsa
     */
    public static boolean isOwner(Long resourceOwnerId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(resourceOwnerId);
    }

    /**
     * Verifica se l'utente corrente può accedere alla risorsa
     * (è il proprietario OPPURE è admin)
     */
    public static boolean canAccess(Long resourceOwnerId) {
        return isOwner(resourceOwnerId) || isAdmin();
    }
}
