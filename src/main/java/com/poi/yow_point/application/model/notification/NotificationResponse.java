package com.poi.yow_point.application.model.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from the Notification Service API.
 * Generic response for notification operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    /**
     * Response message from the notification service
     */
    @JsonProperty("message")
    private String message;
}
