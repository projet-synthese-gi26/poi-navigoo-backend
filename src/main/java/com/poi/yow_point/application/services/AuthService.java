package com.poi.yow_point.application.services;

import com.poi.yow_point.application.exceptions.AccountLockedException;
import com.poi.yow_point.application.exceptions.AuthenticationException;
import com.poi.yow_point.application.exceptions.TokenExpiredException;
import com.poi.yow_point.application.mappers.UserMapper;
import com.poi.yow_point.application.model.Role;
import com.poi.yow_point.infrastructure.clients.NotificationServiceClient;
import com.poi.yow_point.infrastructure.configuration.AuthProperties;
import com.poi.yow_point.infrastructure.configuration.JwtProperties;
import com.poi.yow_point.infrastructure.configuration.NotificationServiceProperties;
import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.infrastructure.entities.RefreshToken;
import com.poi.yow_point.infrastructure.repositories.appUser.AppUserRepository;
import com.poi.yow_point.infrastructure.repositories.refreshToken.RefreshTokenRepository;
import com.poi.yow_point.infrastructure.security.JwtUtil;
import com.poi.yow_point.presentation.dto.auth.AuthResponse;
import com.poi.yow_point.presentation.dto.auth.LoginRequest;
import com.poi.yow_point.presentation.dto.auth.RegisterRequest;
import com.poi.yow_point.presentation.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling authentication operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final NotificationServiceClient notificationClient;
    private final JwtProperties jwtProperties;
    private final AuthProperties authProperties;
    private final NotificationServiceProperties notificationProperties;

    /**
     * Register a new user
     */
    @Transactional
    public Mono<AuthResponse> register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new AuthenticationException("Email already registered"));
                    }
                    return userRepository.existsByUsername(request.getUsername());
                })
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new AuthenticationException("Username already taken"));
                    }

                    // Create new user
                    AppUser newUser = AppUser.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .passwordHash(passwordEncoder.encode(request.getPassword()))
                            .organizationId(request.getOrganizationId())
                            .role(request.getRole() != null ? request.getRole() : Role.USER)
                            .isActive(true)
                            .emailVerified(false)
                            .failedLoginAttempts(0)
                            .createdAt(OffsetDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                })
                .flatMap(savedUser -> {
                    // Send welcome email
                    sendWelcomeEmail(savedUser).subscribe();

                    // Generate tokens
                    String accessToken = jwtUtil.generateAccessToken(
                            savedUser.getUserId(),
                            savedUser.getEmail(),
                            savedUser.getRole()
                    );
                    String refreshToken = jwtUtil.generateRefreshToken(
                            savedUser.getUserId(),
                            savedUser.getEmail()
                    );

                    // Save refresh token
                    return saveRefreshToken(savedUser.getUserId(), refreshToken)
                            .then(Mono.just(AuthResponse.of(
                                    accessToken,
                                    refreshToken,
                                    jwtProperties.getAccessTokenExpiration(),
                                    userMapper.toDTO(savedUser)
                            )));
                });
    }

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public Mono<AuthResponse> login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmailOrUsername());

        // Find user by email or username
        return findUserByEmailOrUsername(request.getEmailOrUsername())
                .switchIfEmpty(Mono.error(new AuthenticationException("Invalid credentials")))
                .flatMap(user -> {
                    // Check if account is locked
                    if (user.getAccountLockedUntil() != null && 
                        OffsetDateTime.now().isBefore(user.getAccountLockedUntil())) {
                        return Mono.error(new AccountLockedException(
                                "Account is locked due to too many failed login attempts",
                                user.getAccountLockedUntil()
                        ));
                    }

                    // Verify password
                    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                        return handleFailedLogin(user)
                                .then(Mono.error(new AuthenticationException("Invalid credentials")));
                    }

                    // Reset failed attempts and update last login
                    return handleSuccessfulLogin(user)
                            .flatMap(updatedUser -> {
                                // Generate tokens
                                String accessToken = jwtUtil.generateAccessToken(
                                        updatedUser.getUserId(),
                                        updatedUser.getEmail(),
                                        updatedUser.getRole()
                                );
                                String refreshToken = jwtUtil.generateRefreshToken(
                                        updatedUser.getUserId(),
                                        updatedUser.getEmail()
                                );

                                // Save refresh token
                                return saveRefreshToken(updatedUser.getUserId(), refreshToken)
                                        .then(Mono.just(AuthResponse.of(
                                                accessToken,
                                                refreshToken,
                                                jwtProperties.getAccessTokenExpiration(),
                                                userMapper.toDTO(updatedUser)
                                        )));
                            });
                });
    }

    /**
     * Logout user by revoking refresh tokens
     */
    @Transactional
    public Mono<Void> logout(UUID userId) {
        log.info("Logging out user: {}", userId);
        return refreshTokenRepository.revokeAllByUserId(userId);
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public Mono<AuthResponse> refreshToken(String refreshTokenString) {
        log.info("Refreshing access token");

        // Validate refresh token
        if (!jwtUtil.validateToken(refreshTokenString)) {
            return Mono.error(new TokenExpiredException("Invalid or expired refresh token"));
        }

        // Find refresh token in database
        return refreshTokenRepository.findByToken(refreshTokenString)
                .switchIfEmpty(Mono.error(new TokenExpiredException("Refresh token not found")))
                .flatMap(refreshToken -> {
                    // Check if token is valid
                    if (!refreshToken.isValid()) {
                        return Mono.error(new TokenExpiredException("Refresh token is expired or revoked"));
                    }

                    // Get user
                    return userRepository.findById(refreshToken.getUserId())
                            .switchIfEmpty(Mono.error(new AuthenticationException("User not found")))
                            .flatMap(user -> {
                                // Generate new access token
                                String newAccessToken = jwtUtil.generateAccessToken(
                                        user.getUserId(),
                                        user.getEmail(),
                                        user.getRole()
                                );

                                return Mono.just(AuthResponse.of(
                                        newAccessToken,
                                        refreshTokenString,
                                        jwtProperties.getAccessTokenExpiration(),
                                        userMapper.toDTO(user)
                                ));
                            });
                });
    }

    /**
     * Get current user profile
     */
    public Mono<UserDTO> getCurrentUser(UUID userId) {
        log.info("Getting current user profile: {}", userId);
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new AuthenticationException("User not found")))
                .map(userMapper::toDTO);
    }

    // Helper methods

    private Mono<AppUser> findUserByEmailOrUsername(String emailOrUsername) {
        if (emailOrUsername.contains("@")) {
            return userRepository.findByEmail(emailOrUsername);
        } else {
            return userRepository.findByUsername(emailOrUsername);
        }
    }

    private Mono<Void> handleFailedLogin(AppUser user) {
        return userRepository.incrementFailedLoginAttempts(user.getUserId())
                .then(userRepository.findById(user.getUserId()))
                .flatMap(updatedUser -> {
                    if (updatedUser.getFailedLoginAttempts() >= authProperties.getMaxFailedAttempts()) {
                        OffsetDateTime lockUntil = OffsetDateTime.now()
                                .plusMinutes(authProperties.getLockoutDurationMinutes());
                        return userRepository.lockAccount(user.getUserId(), lockUntil);
                    }
                    return Mono.empty();
                });
    }

    private Mono<AppUser> handleSuccessfulLogin(AppUser user) {
        return userRepository.resetFailedLoginAttempts(user.getUserId())
                .then(userRepository.updateLastLogin(user.getUserId()))
                .then(userRepository.findById(user.getUserId()));
    }

    private Mono<RefreshToken> saveRefreshToken(UUID userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(OffsetDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000))
                .revoked(false)
                .createdAt(OffsetDateTime.now())
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private Mono<Void> sendWelcomeEmail(AppUser user) {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", user.getUsername());
        data.put("email", user.getEmail());

        Long templateId = notificationProperties.getTemplate().getTemplateId("user-registration");
        if (templateId == null) {
            log.warn("Welcome email template ID not configured");
            return Mono.empty();
        }

        return notificationClient.sendEmail(templateId, user.getEmail(), data)
                .doOnSuccess(response -> log.info("Welcome email sent to: {}", user.getEmail()))
                .doOnError(error -> log.error("Failed to send welcome email: {}", error.getMessage()))
                .then();
    }
}

