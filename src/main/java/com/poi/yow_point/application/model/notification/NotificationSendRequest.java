package com.poi.yow_point.application.model.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for sending notifications via the Notification Service API.
 * Corresponds to the POST /api/v1/notifications/send endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSendRequest {

    /**
     * Type of notification: EMAIL, SMS, WHATSAPP, PUSH, or PULL
     */
    @JsonProperty("notificationType")
    private NotificationType notificationType;

    /**
     * Template ID to use for the notification
     */
    @JsonProperty("templateId")
    private Long templateId;

    /**
     * List of recipients (email addresses, phone numbers, or device tokens)
     */
    @JsonProperty("to")
    private List<String> to;

    /**
     * Data to populate template variables (e.g., {{userName}}, {{poiName}})
     */
    @JsonProperty("data")
    private Map<String, Object> data;

    /**
     * Notification type enum matching the Notification Service API
     */
    public enum NotificationType {
        EMAIL,
        SMS,
        WHATSAPP,
        PUSH,
        PULL
    }
}
