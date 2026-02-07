package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.PasswordService;
import com.poi.yow_point.presentation.dto.password.ForgotPasswordRequest;
import com.poi.yow_point.presentation.dto.password.ResetPasswordRequest;
import com.poi.yow_point.presentation.dto.password.VerifyOTPRequest;
import com.poi.yow_point.presentation.dto.password.VerifyPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for password management endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Tag(name = "Password Management", description = "Password reset and verification endpoints")
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot")
    @Operation(summary = "Forgot password", description = "Request password reset OTP email")
    public Mono<ResponseEntity<Map<String, String>>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        return passwordService.forgotPassword(request.getEmail())
                .then(Mono.just(ResponseEntity.ok(Map.of(
                        "message", "If the email exists, a password reset code has been sent"
                ))));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify OTP", description = "Verify if the provided OTP code is valid")
    public Mono<ResponseEntity<Map<String, Object>>> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        log.info("OTP verification request for email: {}", request.getEmail());
        return passwordService.verifyOTP(request.getEmail(), request.getCode())
                .map(isValid -> {
                    if (isValid) {
                        return ResponseEntity.ok(Map.of(
                                "valid", true,
                                "message", "Code is valid"
                        ));
                    } else {
                        return ResponseEntity.badRequest().body(Map.of(
                                "valid", false,
                                "message", "Invalid or expired code"
                        ));
                    }
                });
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password", description = "Reset password using reset code")
    public Mono<ResponseEntity<Map<String, String>>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset request received for email: {}", request.getEmail());
        return passwordService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword())
                .then(Mono.just(ResponseEntity.ok(Map.of(
                        "message", "Password has been reset successfully"
                ))));
    }

    @PostMapping("/check")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Verify password", description = "Verify if provided password matches current password")
    public Mono<ResponseEntity<Map<String, Boolean>>> verifyPassword(
            @Valid @RequestBody VerifyPasswordRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        log.info("Password verification request for user: {}", userId);
        return passwordService.verifyPassword(userId, request.getPassword())
                .map(isValid -> ResponseEntity.ok(Map.of("valid", isValid)));
    }
}
