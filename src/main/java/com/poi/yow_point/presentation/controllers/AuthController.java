package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.AuthService;
import com.poi.yow_point.presentation.dto.auth.AuthResponse;
import com.poi.yow_point.presentation.dto.auth.LoginRequest;
import com.poi.yow_point.presentation.dto.auth.RefreshTokenRequest;
import com.poi.yow_point.presentation.dto.auth.RegisterRequest;
import com.poi.yow_point.presentation.dto.user.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST controller for authentication endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        return authService.register(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for: {}", request.getEmailOrUsername());
        return authService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/logout/{userId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Logout user", description = "Revoke all refresh tokens for the user")
    public Mono<ResponseEntity<Void>> logout(@PathVariable UUID userId) {
        log.info("Logout request received for user: {}", userId);
        return authService.logout(userId)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");
        return authService.refreshToken(request.getRefreshToken())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get current user", description = "Get authenticated user profile")
    public Mono<ResponseEntity<UserDTO>> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        log.info("Get current user request for: {}", userId);
        return authService.getCurrentUser(userId)
                .map(ResponseEntity::ok);
    }
}
