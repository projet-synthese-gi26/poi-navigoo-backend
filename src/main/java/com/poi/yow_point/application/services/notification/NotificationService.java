package com.poi.yow_point.application.services.notification;

import com.poi.yow_point.infrastructure.clients.NotificationServiceClient;
import com.poi.yow_point.infrastructure.configuration.NotificationServiceProperties;
import com.poi.yow_point.presentation.dto.AppUserDTO;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for handling notification operations related to POI events.
 * Orchestrates sending notifications via email and WhatsApp.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationServiceClient notificationClient;
    private final NotificationServiceProperties properties;

    /**
     * Send notifications when a new POI is created.
     * Sends both email and WhatsApp notifications to the creator.
     * 
     * @param poi The created POI
     * @param creator The user who created the POI
     * @return Mono that completes when notifications are sent (or fails gracefully)
     */
    public Mono<Void> notifyPoiCreated(PointOfInterestDTO poi, AppUserDTO creator) {
        log.info("Sending POI creation notifications for POI: {} to user: {}", 
                poi.getPoiName(), creator.getUsername());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", creator.getUsername());
        templateData.put("poiName", poi.getPoiName());
        templateData.put("poiType", poi.getPoiType() != null ? poi.getPoiType().toString() : "N/A");
        templateData.put("poiCity", poi.getAddressCity() != null ? poi.getAddressCity() : "N/A");
        templateData.put("poiDescription", poi.getPoiDescription() != null ? poi.getPoiDescription() : "");
        
        // Add missing placeholder: creationDate
        String creationDate = poi.getCreatedAt() != null 
                ? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(java.time.ZoneId.systemDefault())
                    .format(poi.getCreatedAt())
                : java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(java.time.ZoneId.systemDefault())
                    .format(java.time.Instant.now());
        templateData.put("creationDate", creationDate);

        Mono<Void> emailNotification = Mono.empty();
        Mono<Void> whatsappNotification = Mono.empty();

        // Send email if user has email
        if (creator.getEmail() != null && !creator.getEmail().isEmpty()) {
            emailNotification = notificationClient.sendEmail(
                    properties.getTemplate().getPoiCreatedEmail(),
                    creator.getEmail(),
                    templateData
            )
            .doOnSuccess(response -> log.info("Email notification sent to {}", creator.getEmail()))
            .onErrorResume(error -> {
                log.error("Failed to send email notification to {}: {}", 
                        creator.getEmail(), error.getMessage());
                return Mono.empty(); // Don't fail the operation
            })
            .then();
        }

        // Send WhatsApp if user has phone
        if (creator.getPhone() != null && !creator.getPhone().isEmpty()) {
            // Refine phone number: digits only for WhatsApp (many gateways prefer this)
            String rawPhone = creator.getPhone().trim();
            String cleanPhone = rawPhone.replaceAll("\\D", "");
            
            log.debug("Formatting phone number for WhatsApp: original='{}', cleaned='{}'", rawPhone, cleanPhone);
            
            whatsappNotification = notificationClient.sendWhatsApp(
                    properties.getTemplate().getPoiCreatedWhatsapp(),
                    cleanPhone,
                    templateData
            )
            .doOnSuccess(response -> log.info("WhatsApp notification sent to {} (formatted: {})", creator.getPhone(), cleanPhone))
            .onErrorResume(error -> {
                log.error("Failed to send WhatsApp notification to {}: {}", 
                        creator.getPhone(), error.getMessage());
                return Mono.empty(); // Don't fail the operation
            })
            .then();
        }

        // Execute both notifications in parallel
        return Mono.when(emailNotification, whatsappNotification)
                .doOnSuccess(v -> log.info("All POI creation notifications sent successfully"))
                .onErrorResume(error -> {
                    log.error("Error sending POI creation notifications: {}", error.getMessage());
                    return Mono.empty(); // Don't fail the POI creation
                });
    }

    /**
     * Send notifications when a POI is updated by a different user.
     * Notifies the original creator about the modification.
     * 
     * @param poi The updated POI
     * @param creator The original creator of the POI
     * @param updater The user who updated the POI
     * @return Mono that completes when notifications are sent (or fails gracefully)
     */
    public Mono<Void> notifyPoiUpdated(PointOfInterestDTO poi, AppUserDTO creator, AppUserDTO updater) {
        log.info("Sending POI update notifications for POI: {} to creator: {} (updated by: {})", 
                poi.getPoiName(), creator.getUsername(), updater.getUsername());

        Map<String, Object> templateData = new HashMap<>();
        // Match user's template placeholders: userName (creator) and updatedBy (updater)
        templateData.put("userName", creator.getUsername());
        templateData.put("updatedBy", updater.getUsername());
        templateData.put("poiName", poi.getPoiName());
        templateData.put("poiType", poi.getPoiType() != null ? poi.getPoiType().toString() : "N/A");
        templateData.put("poiCity", poi.getAddressCity() != null ? poi.getAddressCity() : "N/A");
        
        // Add missing placeholder: updateDate
        String updateDate = poi.getUpdatedAt() != null 
                ? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(java.time.ZoneId.systemDefault())
                    .format(poi.getUpdatedAt())
                : java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(java.time.ZoneId.systemDefault())
                    .format(java.time.Instant.now());
        templateData.put("updateDate", updateDate);

        Mono<Void> emailNotification = Mono.empty();
        Mono<Void> whatsappNotification = Mono.empty();

        // Send email if creator has email
        if (creator.getEmail() != null && !creator.getEmail().isEmpty()) {
            emailNotification = notificationClient.sendEmail(
                    properties.getTemplate().getPoiUpdatedEmail(),
                    creator.getEmail(),
                    templateData
            )
            .doOnSuccess(response -> log.info("Update email notification sent to {}", creator.getEmail()))
            .onErrorResume(error -> {
                log.error("Failed to send update email notification to {}: {}", 
                        creator.getEmail(), error.getMessage());
                return Mono.empty();
            })
            .then();
        }

        // Send WhatsApp if creator has phone
        if (creator.getPhone() != null && !creator.getPhone().isEmpty()) {
            // Refine phone number: digits only for WhatsApp
            String rawPhone = creator.getPhone().trim();
            String cleanPhone = rawPhone.replaceAll("\\D", "");

            log.debug("Formatting phone number for WhatsApp update: original='{}', cleaned='{}'", rawPhone, cleanPhone);

            whatsappNotification = notificationClient.sendWhatsApp(
                    properties.getTemplate().getPoiUpdatedWhatsapp(),
                    cleanPhone,
                    templateData
            )
            .doOnSuccess(response -> log.info("Update WhatsApp notification sent to {} (formatted: {})", creator.getPhone(), cleanPhone))
            .onErrorResume(error -> {
                log.error("Failed to send update WhatsApp notification to {}: {}", 
                        creator.getPhone(), error.getMessage());
                return Mono.empty();
            })
            .then();
        }

        // Execute both notifications in parallel
        return Mono.when(emailNotification, whatsappNotification)
                .doOnSuccess(v -> log.info("All POI update notifications sent successfully"))
                .onErrorResume(error -> {
                    log.error("Error sending POI update notifications: {}", error.getMessage());
                    return Mono.empty(); // Don't fail the POI update
                });
    }

    /**
     * Send monthly digest email with POI recommendations.
     * 
     * @param user The user to send the digest to
     * @param recommendations List of recommended POIs
     * @return Mono that completes when digest is sent
     */
    public Mono<Void> sendMonthlyDigest(AppUserDTO user, List<PointOfInterestDTO> recommendations) {
        log.info("Sending monthly digest to user: {} with {} recommendations", 
                user.getUsername(), recommendations.size());

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("User {} has no email, skipping monthly digest", user.getUsername());
            return Mono.empty();
        }

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", user.getUsername());
        templateData.put("poiCount", String.valueOf(recommendations.size()));
        
        // Build a summary of recommendations
        StringBuilder poiSummary = new StringBuilder();
        for (int i = 0; i < Math.min(recommendations.size(), 10); i++) {
            PointOfInterestDTO poi = recommendations.get(i);
            poiSummary.append(String.format("%d. %s - %s\n", 
                    i + 1, 
                    poi.getPoiName(), 
                    poi.getAddressCity() != null ? poi.getAddressCity() : ""));
        }
        templateData.put("poiSummary", poiSummary.toString());

        return notificationClient.sendEmail(
                properties.getTemplate().getMonthlyDigestEmail(),
                user.getEmail(),
                templateData
        )
        .doOnSuccess(response -> log.info("Monthly digest sent to {}", user.getEmail()))
        .onErrorResume(error -> {
            log.error("Failed to send monthly digest to {}: {}", 
                    user.getEmail(), error.getMessage());
            return Mono.empty();
        })
        .then();
    }
}
