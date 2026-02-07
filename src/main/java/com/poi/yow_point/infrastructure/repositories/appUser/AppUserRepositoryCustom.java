package com.poi.yow_point.infrastructure.repositories.appUser;

import com.poi.yow_point.infrastructure.entities.AppUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppUserRepositoryCustom {
    
    /**
     * Find a user by email address
     */
    Mono<AppUser> findByEmail(String email);

    /**
     * Find a user by username
     */
    Mono<AppUser> findByUsername(String username);

    /**
     * Check if a user exists with the given email
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Check if a user exists with the given username
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Update user's last login timestamp
     */
    Mono<Void> updateLastLogin(UUID userId);

    /**
     * Increment failed login attempts
     */
    Mono<Void> incrementFailedLoginAttempts(UUID userId);

    /**
     * Reset failed login attempts to zero
     */
    Mono<Void> resetFailedLoginAttempts(UUID userId);

    /**
     * Lock user account until specified time
     */
    Mono<Void> lockAccount(UUID userId, java.time.OffsetDateTime until);

    /**
     * Unlock user account
     */
    Mono<Void> unlockAccount(UUID userId);

    /**
     * Check if users exist for an organization
     */
    Mono<Boolean> existsByOrgId(UUID orgId);

    /**
     * Count active users by organization
     */
    Mono<Long> countActiveUsersByOrgId(UUID orgId);

    /**
     * Find users by organization and active status
     */
    Flux<AppUser> findByOrgIdAndIsActive(UUID orgId, Boolean isActive);

    /**
     * Find users by role
     */
    Flux<AppUser> findByRole(String role);
}
