package com.poi.yow_point.infrastructure.repositories.refreshToken;

import com.poi.yow_point.infrastructure.entities.RefreshToken;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken, UUID> {

    /**
     * Find a refresh token by its token string
     */
    Mono<RefreshToken> findByToken(String token);

    /**
     * Find all refresh tokens for a specific user
     */
    Flux<RefreshToken> findByUserId(UUID userId);

    /**
     * Delete all refresh tokens for a specific user
     */
    Mono<Void> deleteByUserId(UUID userId);

    /**
     * Delete all expired tokens
     */
    @Query("DELETE FROM refresh_token WHERE expires_at < :now")
    Mono<Void> deleteExpiredTokens(OffsetDateTime now);

    /**
     * Revoke all tokens for a user
     */
    @Query("UPDATE refresh_token SET revoked = true WHERE user_id = :userId")
    Mono<Void> revokeAllByUserId(UUID userId);
}
