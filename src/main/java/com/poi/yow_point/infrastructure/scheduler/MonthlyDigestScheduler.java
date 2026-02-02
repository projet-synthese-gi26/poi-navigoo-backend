package com.poi.yow_point.infrastructure.scheduler;

import com.poi.yow_point.application.services.appUser.AppUserService;
import com.poi.yow_point.application.services.digest.DigestRecommendationService;
import com.poi.yow_point.application.services.notification.NotificationService;
import com.poi.yow_point.infrastructure.configuration.NotificationServiceProperties;
import com.poi.yow_point.presentation.dto.AppUserDTO;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Scheduled task for sending monthly digest emails to users.
 * Runs on a configurable cron schedule (default: 1st day of month at 9 AM).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notification.digest", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonthlyDigestScheduler {

    private final AppUserService appUserService;
    private final DigestRecommendationService recommendationService;
    private final NotificationService notificationService;
    private final NotificationServiceProperties properties;

    /**
     * Scheduled task to send monthly digest emails.
     * Cron expression is configured via notification.digest.schedule property.
     */
    @Scheduled(cron = "${notification.digest.schedule:0 0 9 1 * ?}")
    public void sendMonthlyDigests() {
        log.info("Starting monthly digest job...");

        long startTime = System.currentTimeMillis();

        appUserService.getAllUsers()
                .filter(user -> user.getEmail() != null && !user.getEmail().isEmpty())
                .filter(AppUserDTO::getIsActive)
                .flatMap(user -> {
                    log.debug("Processing monthly digest for user: {}", user.getUsername());

                    // Generate recommendations for the user
                    return recommendationService.generatePersonalizedRecommendations(
                            user.getUserId(), 
                            user.getOrganizationId()
                    )
                    .collectList()
                    .flatMap(recommendations -> {
                        if (recommendations.isEmpty()) {
                            log.debug("No recommendations for user: {}, skipping digest", user.getUsername());
                            return Mono.empty();
                        }

                        // Send digest email
                        return notificationService.sendMonthlyDigest(user, recommendations)
                                .doOnSuccess(v -> log.info("Monthly digest sent to: {}", user.getEmail()))
                                .onErrorResume(error -> {
                                    log.error("Failed to send monthly digest to {}: {}", 
                                            user.getEmail(), error.getMessage());
                                    return Mono.empty();
                                });
                    });
                })
                .doOnComplete(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Monthly digest job completed in {} ms", duration);
                })
                .doOnError(error -> log.error("Error in monthly digest job: {}", error.getMessage()))
                .subscribe(); // Fire and forget
    }
}
