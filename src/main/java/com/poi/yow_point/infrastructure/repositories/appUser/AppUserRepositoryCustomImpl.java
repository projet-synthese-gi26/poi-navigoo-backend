package com.poi.yow_point.infrastructure.repositories.appUser;

import com.poi.yow_point.infrastructure.entities.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AppUserRepositoryCustomImpl implements AppUserRepositoryCustom {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<AppUser> findByEmail(String email) {
        return databaseClient.sql("SELECT * FROM app_user WHERE email = :email")
                .bind("email", email)
                .map((row, metadata) -> mapRowToAppUser(row))
                .one();
    }

    @Override
    public Mono<AppUser> findByUsername(String username) {
        return databaseClient.sql("SELECT * FROM app_user WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> mapRowToAppUser(row))
                .one();
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return databaseClient.sql("SELECT COUNT(*) FROM app_user WHERE email = :email")
                .bind("email", email)
                .map((row, metadata) -> row.get(0, Long.class))
                .one()
                .map(count -> count > 0);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return databaseClient.sql("SELECT COUNT(*) FROM app_user WHERE username = :username")
                .bind("username", username)
                .map((row, metadata) -> row.get(0, Long.class))
                .one()
                .map(count -> count > 0);
    }

    @Override
    public Mono<Void> updateLastLogin(UUID userId) {
        return databaseClient.sql("UPDATE app_user SET last_login_at = :now WHERE user_id = :userId")
                .bind("now", OffsetDateTime.now())
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Void> incrementFailedLoginAttempts(UUID userId) {
        return databaseClient.sql("UPDATE app_user SET failed_login_attempts = failed_login_attempts + 1 WHERE user_id = :userId")
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Void> resetFailedLoginAttempts(UUID userId) {
        return databaseClient.sql("UPDATE app_user SET failed_login_attempts = 0 WHERE user_id = :userId")
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Void> lockAccount(UUID userId, OffsetDateTime until) {
        return databaseClient.sql("UPDATE app_user SET account_locked_until = :until WHERE user_id = :userId")
                .bind("until", until)
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Void> unlockAccount(UUID userId) {
        return databaseClient.sql("UPDATE app_user SET account_locked_until = NULL, failed_login_attempts = 0 WHERE user_id = :userId")
                .bind("userId", userId)
                .then();
    }

    @Override
    public Mono<Boolean> existsByOrgId(UUID orgId) {
        return databaseClient.sql("SELECT COUNT(*) FROM app_user WHERE organization_id = :orgId")
                .bind("orgId", orgId)
                .map((row, metadata) -> row.get(0, Long.class))
                .one()
                .map(count -> count > 0);
    }

    @Override
    public Mono<Long> countActiveUsersByOrgId(UUID orgId) {
        return databaseClient.sql("SELECT COUNT(*) FROM app_user WHERE organization_id = :orgId AND is_active = true")
                .bind("orgId", orgId)
                .map((row, metadata) -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Flux<AppUser> findByOrgIdAndIsActive(UUID orgId, Boolean isActive) {
        return databaseClient.sql("SELECT * FROM app_user WHERE organization_id = :orgId AND is_active = :isActive")
                .bind("orgId", orgId)
                .bind("isActive", isActive)
                .map((row, metadata) -> mapRowToAppUser(row))
                .all();
    }

    @Override
    public Flux<AppUser> findByRole(String role) {
        return databaseClient.sql("SELECT * FROM app_user WHERE role = :role")
                .bind("role", role)
                .map((row, metadata) -> mapRowToAppUser(row))
                .all();
    }

    private AppUser mapRowToAppUser(io.r2dbc.spi.Row row) {
        return AppUser.builder()
                .userId(row.get("user_id", UUID.class))
                .organizationId(row.get("organization_id", UUID.class))
                .username(row.get("username", String.class))
                .email(row.get("email", String.class))
                .phone(row.get("phone", String.class))
                .passwordHash(row.get("password_hash", String.class))
                .role(com.poi.yow_point.application.model.Role.valueOf(row.get("role", String.class)))
                .isActive(row.get("is_active", Boolean.class))
                .createdAt(row.get("created_at", OffsetDateTime.class))
                .emailVerified(row.get("email_verified", Boolean.class))
                .lastLoginAt(row.get("last_login_at", OffsetDateTime.class))
                .failedLoginAttempts(row.get("failed_login_attempts", Integer.class))
                .accountLockedUntil(row.get("account_locked_until", OffsetDateTime.class))
                .build();
    }
}