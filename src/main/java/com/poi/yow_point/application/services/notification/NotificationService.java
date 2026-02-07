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
     * Send notification when a POI is submitted for validation.
     * 
     * @param poi The submitted POI
     * @param user The user who submitted the POI
     * @return Mono that completes when notification is sent
     */
    public Mono<Void> notifyPoiSubmitted(PointOfInterestDTO poi, AppUserDTO user) {
        log.info("Sending POI submission notification for POI: {} to user: {}", 
                poi.getPoiName(), user.getUsername());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", user.getUsername());
        templateData.put("poiName", poi.getPoiName());
        
        String submissionDate = formatInstant(poi.getCreatedAt());
        templateData.put("submissionDate", submissionDate);

        return sendEmailNotification(properties.getTemplate().getEmail().getPoiSubmitted(), user.getEmail(), templateData);
    }

    /**
     * Send notification when a POI is approved.
     * 
     * @param poi The approved POI
     * @param user The user who submitted the POI
     * @return Mono that completes when notification is sent
     */
    public Mono<Void> notifyPoiApproved(PointOfInterestDTO poi, AppUserDTO user) {
        log.info("Sending POI approval notification for POI: {} to user: {}", 
                poi.getPoiName(), user.getUsername());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", user.getUsername());
        templateData.put("poiName", poi.getPoiName());
        
        String approvalDate = formatInstant(java.time.Instant.now());
        templateData.put("approvalDate", approvalDate);

        return sendEmailNotification(properties.getTemplate().getEmail().getPoiApproved(), user.getEmail(), templateData);
    }

    /**
     * Send notification when a POI is rejected.
     * 
     * @param poi The rejected POI
     * @param user The user who submitted the POI
     * @return Mono that completes when notification is sent
     */
    public Mono<Void> notifyPoiRejected(PointOfInterestDTO poi, AppUserDTO user) {
        log.info("Sending POI rejection notification for POI: {} to user: {}", 
                poi.getPoiName(), user.getUsername());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", user.getUsername());
        templateData.put("poiName", poi.getPoiName());
        
        String submissionDate = formatInstant(poi.getCreatedAt());
        templateData.put("submissionDate", submissionDate);

        return sendEmailNotification(properties.getTemplate().getEmail().getPoiRejected(), user.getEmail(), templateData);
    }

    /**
     * Helper method to send email notification.
     */
    private Mono<Void> sendEmailNotification(Long templateId, String email, Map<String, Object> data) {
        if (email == null || email.isEmpty()) {
            log.warn("Recipient email is missing, skipping notification");
            return Mono.empty();
        }

        return notificationClient.sendEmail(templateId, email, data)
                .doOnSuccess(response -> log.info("Email notification (template: {}) sent to {}", templateId, email))
                .onErrorResume(error -> {
                    log.error("Failed to send email notification (template: {}) to {}: {}", 
                            templateId, email, error.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    /**
     * Helper to format Instant to String.
     */
    private String formatInstant(java.time.Instant instant) {
        if (instant == null) instant = java.time.Instant.now();
        return java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant);
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
        
        // Month name in French
        String month = java.time.Month.from(java.time.LocalDate.now())
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH);
        templateData.put("month", month);
        
        // Build summary
        StringBuilder poiSummary = new StringBuilder();
        StringBuilder poiSummaryHtml = new StringBuilder();
        
        for (int i = 0; i < Math.min(recommendations.size(), 10); i++) {
            PointOfInterestDTO poi = recommendations.get(i);
            String city = poi.getAddressCity() != null ? poi.getAddressCity() : "N/A";
            
            poiSummary.append(String.format("- %s (%s)\n", poi.getPoiName(), city));
            poiSummaryHtml.append(String.format("<li><strong>%s</strong> - %s</li>", poi.getPoiName(), city));
        }
        
        templateData.put("poiSummary", poiSummary.toString());
        templateData.put("poiSummaryHtml", poiSummaryHtml.toString());

        return sendEmailNotification(properties.getTemplate().getMonthlyDigestEmail(), user.getEmail(), templateData);
    }
}
