package com.poi.yow_point.application.services;

import com.poi.yow_point.application.exceptions.AuthenticationException;
import com.poi.yow_point.application.exceptions.TokenExpiredException;
import com.poi.yow_point.infrastructure.clients.NotificationServiceClient;
import com.poi.yow_point.infrastructure.configuration.NotificationServiceProperties;
import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.infrastructure.entities.PasswordResetToken;
import com.poi.yow_point.infrastructure.repositories.appUser.AppUserRepository;
import com.poi.yow_point.infrastructure.repositories.passwordResetToken.PasswordResetTokenRepository;
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
 * Service for handling password management operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final AppUserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationServiceClient notificationClient;
    private final NotificationServiceProperties notificationProperties;

    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 15;

    /**
     * Initiate password reset process by generating a 6-digit OTP
     */
    @Transactional
    public Mono<Void> forgotPassword(String email) {
        log.info("Password reset requested for email: {}", email);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> {
                    // Don't reveal if email exists or not for security
                    log.warn("Password reset requested for non-existent email: {}", email);
                    return Mono.empty();
                }))
                .flatMap(user -> {
                    // Generate 6-digit OTP
                    String otpCode = String.format("%06d", new java.util.Random().nextInt(1000000));
                    
                    PasswordResetToken resetToken = PasswordResetToken.builder()
                            .userId(user.getUserId())
                            .token(otpCode)
                            .expiresAt(OffsetDateTime.now().plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES))
                            .used(false)
                            .createdAt(OffsetDateTime.now())
                            .build();

                    // Delete existing tokens for this user first
                    return passwordResetTokenRepository.deleteByUserId(user.getUserId())
                            .then(passwordResetTokenRepository.save(resetToken))
                            .flatMap(savedToken -> sendPasswordResetOTPEmail(user, otpCode, savedToken.getExpiresAt()))
                            .then();
                })
                .then();
    }

    /**
     * Verify if the provided OTP code is valid for the given email
     */
    public Mono<Boolean> verifyOTP(String email, String code) {
        log.info("Verifying OTP code for email: {}", email);
        
        return passwordResetTokenRepository.findByEmailAndCode(email, code)
                .map(PasswordResetToken::isValid)
                .defaultIfEmpty(false);
    }

    /**
     * Reset password using email and OTP code
     */
    @Transactional
    public Mono<Void> resetPassword(String email, String code, String newPassword) {
        log.info("Password reset attempt for email: {} with code", email);

        return passwordResetTokenRepository.findByEmailAndCode(email, code)
                .switchIfEmpty(Mono.error(new TokenExpiredException("Invalid or expired reset code")))
                .flatMap(resetToken -> {
                    // Validate token
                    if (!resetToken.isValid()) {
                        return Mono.error(new TokenExpiredException("Reset code is expired or already used"));
                    }

                    // Get user and update password
                    return userRepository.findById(resetToken.getUserId())
                            .switchIfEmpty(Mono.error(new AuthenticationException("User not found")))
                            .flatMap(user -> {
                                user.setPasswordHash(passwordEncoder.encode(newPassword));
                                return userRepository.save(user)
                                        .flatMap(savedUser -> sendPasswordResetSuccessEmail(savedUser));
                            })
                            .then(passwordResetTokenRepository.markAsUsed(resetToken.getToken()))
                            .then();
                });
    }

    /**
     * Verify if provided password matches user's current password
     */
    public Mono<Boolean> verifyPassword(UUID userId, String password) {
        log.info("Password verification for user: {}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new AuthenticationException("User not found")))
                .map(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    /**
     * Change user password (requires current password verification)
     */
    @Transactional
    public Mono<Void> changePassword(UUID userId, String currentPassword, String newPassword) {
        log.info("Password change request for user: {}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new AuthenticationException("User not found")))
                .flatMap(user -> {
                    // Verify current password
                    if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                        return Mono.error(new AuthenticationException("Current password is incorrect"));
                    }

                    // Update password
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    return userRepository.save(user);
                })
                .then();
    }

    // Helper methods

    private Mono<Void> sendPasswordResetOTPEmail(AppUser user, String code, OffsetDateTime expiresAt) {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", user.getUsername()); // Matches {{userName}}
        data.put("email", user.getEmail());
        data.put("otpCode", code);
        data.put("expirationDate", formatInstant(expiresAt.toInstant())); // Matches {{expirationDate}}

        Long templateId = notificationProperties.getTemplate().getTemplateId("password-verification");
        if (templateId == null) {
            log.warn("Password verification email template ID not configured");
            return Mono.empty();
        }

        return notificationClient.sendEmail(templateId, user.getEmail(), data)
                .doOnSuccess(response -> log.info("Password verification email sent to: {}", user.getEmail()))
                .doOnError(error -> log.error("Failed to send password verification email: {}", error.getMessage()))
                .then();
    }

    private Mono<Void> sendPasswordResetSuccessEmail(AppUser user) {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", user.getUsername()); // Matches {{userName}}
        data.put("email", user.getEmail());

        Long templateId = notificationProperties.getTemplate().getTemplateId("password-reset");
        if (templateId == null) {
            log.warn("Password reset success email template ID not configured");
            return Mono.empty();
        }

        return notificationClient.sendEmail(templateId, user.getEmail(), data)
                .doOnSuccess(response -> log.info("Password reset success email sent to: {}", user.getEmail()))
                .doOnError(error -> log.error("Failed to send password reset success email: {}", error.getMessage()))
                .then();
    }

    private String formatInstant(java.time.Instant instant) {
        return java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant);
    }
}
