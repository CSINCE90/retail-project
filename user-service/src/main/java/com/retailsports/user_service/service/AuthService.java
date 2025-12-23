package com.retailsports.user_service.service;

import com.retailsports.user_service.dto.request.LoginRequest;
import com.retailsports.user_service.dto.request.RegisterRequest;
import com.retailsports.user_service.dto.response.AuthResponse;
import com.retailsports.user_service.dto.response.UserResponse;
import com.retailsports.user_service.exception.BadRequestException;
import com.retailsports.user_service.exception.UnauthorizedException;
import com.retailsports.user_service.model.RefreshToken;
import com.retailsports.user_service.model.Role;
import com.retailsports.user_service.model.User;
import com.retailsports.user_service.repository.RoleRepository;
import com.retailsports.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final LoginAuditService loginAuditService;

    /**
     * Registrazione nuovo utente
     */
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Verifica che username non esista già
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        // Verifica che email non esista già
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Crea nuovo utente
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .enabled(true)
            .emailVerified(false)
            .build();

        // Assegna ruolo USER di default
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.addRole(userRole);

        // Salva utente
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // TODO: Invia email di verifica

        return convertToUserResponse(savedUser);
    }

    /**
     * Login utente
     */
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Trova utente per username o email
        User user = userRepository.findByUsernameOrEmail(request.getUsername())
            .orElseThrow(() -> {
                // Log tentativo fallito
                loginAuditService.logFailedLogin(request.getUsername(), ipAddress, userAgent, "User not found");
                return new UnauthorizedException("Invalid credentials");
            });

        // Verifica password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Log tentativo fallito
            loginAuditService.logFailedLogin(request.getUsername(), ipAddress, userAgent, "Invalid password");
            throw new UnauthorizedException("Invalid credentials");
        }

        // Verifica che account sia abilitato
        if (!user.getEnabled()) {
            loginAuditService.logFailedLogin(request.getUsername(), ipAddress, userAgent, "Account disabled");
            throw new UnauthorizedException("Account is disabled");
        }

        // Genera JWT tokens
        String accessToken = tokenService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);

        // Log login riuscito
        loginAuditService.logSuccessfulLogin(user, ipAddress, userAgent);

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .tokenType("Bearer")
            .expiresIn(86400000L) // 24 ore
            .user(convertToUserResponse(user))
            .build();
    }

    /**
     * Refresh access token
     */
    public AuthResponse refreshAccessToken(String refreshTokenString) {
        log.info("Refreshing access token");

        // Valida refresh token
        RefreshToken refreshToken = tokenService.validateRefreshToken(refreshTokenString);

        // Genera nuovo access token
        String accessToken = tokenService.generateAccessToken(refreshToken.getUser());

        log.info("Access token refreshed for user: {}", refreshToken.getUser().getUsername());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshTokenString) // Stesso refresh token
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(convertToUserResponse(refreshToken.getUser()))
            .build();
    }

    /**
     * Logout utente
     */
    public void logout(Long userId, String refreshTokenString) {
        log.info("Logging out user: {}", userId);

        // Elimina refresh token
        if (refreshTokenString != null) {
            tokenService.deleteRefreshToken(refreshTokenString);
        }

        log.info("User logged out successfully: {}", userId);
    }

    /**
     * Logout da tutti i dispositivi
     */
    public void logoutAllDevices(Long userId) {
        log.info("Logging out user from all devices: {}", userId);

        tokenService.deleteAllRefreshTokensForUser(userId);

        log.info("User logged out from all devices: {}", userId);
    }

    // ====================================
    // UTILITY METHODS
    // ====================================

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
                .map(Role::getName)
                .collect(Collectors.toSet()))
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
