package com.retailsports.user_service.service;

import com.retailsports.user_service.model.LoginAudit;
import com.retailsports.user_service.model.User;
import com.retailsports.user_service.repository.LoginAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoginAuditService {

    private final LoginAuditRepository loginAuditRepository;

    /**
     * Log login riuscito
     */
    public void logSuccessfulLogin(User user, String ipAddress, String userAgent) {
        LoginAudit audit = LoginAudit.builder()
            .user(user)
            .username(user.getUsername())
            .success(true)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        loginAuditRepository.save(audit);
        log.info("Successful login logged for user: {}", user.getUsername());
    }

    /**
     * Log login fallito
     */
    public void logFailedLogin(String username, String ipAddress, String userAgent, String failureReason) {
        LoginAudit audit = LoginAudit.builder()
            .user(null) // Non abbiamo l'utente per login falliti
            .username(username)
            .success(false)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .failureReason(failureReason)
            .build();

        loginAuditRepository.save(audit);
        log.warn("Failed login attempt for username: {} - Reason: {}", username, failureReason);
    }

    /**
     * Ottieni storico login di un utente con paginazione
     */
    @Transactional(readOnly = true)
    public Page<LoginAudit> getUserLoginHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return loginAuditRepository.findByUserId(userId, pageable);
    }

    /**
     * Ottieni storico login per username (include tentativi falliti)
     */
    @Transactional(readOnly = true)
    public Page<LoginAudit> getLoginHistoryByUsername(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return loginAuditRepository.findByUsername(username, pageable);
    }

    /**
     * Ottieni solo login riusciti di un utente
     */
    @Transactional(readOnly = true)
    public Page<LoginAudit> getSuccessfulLogins(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return loginAuditRepository.findSuccessfulLoginsByUserId(userId, pageable);
    }

    /**
     * Ottieni ultimo login riuscito di un utente
     */
    @Transactional(readOnly = true)
    public LoginAudit getLastSuccessfulLogin(Long userId) {
        Pageable pageable = PageRequest.of(0, 1);
        List<LoginAudit> logins = loginAuditRepository.findLastSuccessfulLogin(userId, pageable);
        
        return logins.isEmpty() ? null : logins.get(0);
    }

    /**
     * Conta tentativi falliti recenti per username
     */
    @Transactional(readOnly = true)
    public long countRecentFailedAttempts(String username, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return loginAuditRepository.countFailedAttemptsSince(username, since);
    }

    /**
     * Verifica se account è bloccato per troppi tentativi falliti
     */
    @Transactional(readOnly = true)
    public boolean isAccountLockedDueToFailedAttempts(String username, int maxAttempts, int hoursWindow) {
        long failedAttempts = countRecentFailedAttempts(username, hoursWindow);
        
        if (failedAttempts >= maxAttempts) {
            log.warn("Account temporarily locked due to {} failed login attempts: {}", failedAttempts, username);
            return true;
        }
        
        return false;
    }

    /**
     * Ottieni tentativi falliti da un IP specifico
     */
    @Transactional(readOnly = true)
    public List<LoginAudit> getFailedAttemptsByIp(String ipAddress) {
        return loginAuditRepository.findFailedAttemptsByIp(ipAddress);
    }

    /**
     * Ottieni statistiche login per periodo
     */
    @Transactional(readOnly = true)
    public LoginStatistics getLoginStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> stats = loginAuditRepository.getLoginStatisticsBetween(startDate, endDate);
        
        Map<Boolean, Long> statsMap = stats.stream()
            .collect(Collectors.toMap(
                row -> (Boolean) row[0],
                row -> (Long) row[1]
            ));
        
        long successfulLogins = statsMap.getOrDefault(true, 0L);
        long failedLogins = statsMap.getOrDefault(false, 0L);
        long totalAttempts = successfulLogins + failedLogins;
        
        double successRate = totalAttempts > 0 
            ? (successfulLogins * 100.0) / totalAttempts 
            : 0.0;
        
        return LoginStatistics.builder()
            .successfulLogins(successfulLogins)
            .failedLogins(failedLogins)
            .totalAttempts(totalAttempts)
            .successRate(successRate)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    /**
     * Ottieni statistiche login giornaliere (ultime 24 ore)
     */
    @Transactional(readOnly = true)
    public LoginStatistics getDailyLoginStatistics() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusHours(24);
        return getLoginStatistics(startDate, endDate);
    }

    /**
     * Ottieni statistiche login settimanali
     */
    @Transactional(readOnly = true)
    public LoginStatistics getWeeklyLoginStatistics() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        return getLoginStatistics(startDate, endDate);
    }

    /**
     * Ottieni statistiche login mensili
     */
    @Transactional(readOnly = true)
    public LoginStatistics getMonthlyLoginStatistics() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        return getLoginStatistics(startDate, endDate);
    }

    // ====================================
    // INNER CLASS - Login Statistics
    // ====================================

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class LoginStatistics {
        private long successfulLogins;
        private long failedLogins;
        private long totalAttempts;
        private double successRate;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}