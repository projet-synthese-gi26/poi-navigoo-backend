package com.poi.yow_point.infrastructure.repositories.passwordResetToken;

import com.poi.yow_point.infrastructure.entities.PasswordResetToken;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PasswordResetTokenRepository extends R2dbcRepository<PasswordResetToken, UUID> {

    /**
     * Find a password reset token by its token string
     */
    Mono<PasswordResetToken> findByToken(String token);

    /**
     * Find all password reset tokens for a specific user
     */
    Flux<PasswordResetToken> findByUserId(UUID userId);

    /**
     * Find a password reset token by user email and token string
     */
    @Query("SELECT prt.* FROM password_reset_token prt JOIN app_user u ON prt.user_id = u.user_id WHERE u.email = :email AND prt.token = :code")
    Mono<PasswordResetToken> findByEmailAndCode(String email, String code);


    /**
     * Delete all password reset tokens for a specific user
     */
    Mono<Void> deleteByUserId(UUID userId);

    /**
     * Delete all expired tokens
     */
    @Query("DELETE FROM password_reset_token WHERE expires_at < :now")
    Mono<Void> deleteExpiredTokens(OffsetDateTime now);

    /**
     * Mark a token as used
     */
    @Query("UPDATE password_reset_token SET used = true WHERE token = :token")
    Mono<Void> markAsUsed(String token);
}
