package com.poi.yow_point.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Notification Service integration.
 * Maps properties from application.properties with prefix "notification".
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationServiceProperties {

    private Service service = new Service();
    private Template template = new Template();
    private Digest digest = new Digest();

    @Data
    public static class Service {
        /**
         * Base URL of the Notification Service API
         */
        private String baseUrl = "https://notification-service.pynfi.com";

        /**
         * Service token for authentication (X-Service-Token header)
         */
        private String token;

        /**
         * Timeout in milliseconds for HTTP requests
         */
        private int timeout = 10000;
    }

    @Data
    public static class Template {
        /**
         * Template ID for POI creation email notification
         */
        private Long poiCreatedEmail = 101L;

        /**
         * Template ID for POI creation WhatsApp notification
         */
        private Long poiCreatedWhatsapp = 102L;

        /**
         * Template ID for POI update email notification
         */
        private Long poiUpdatedEmail = 103L;

        /**
         * Template ID for POI update WhatsApp notification
         */
        private Long poiUpdatedWhatsapp = 104L;

        /**
         * Template ID for monthly digest email
         */
        private Long monthlyDigestEmail = 105L;
    }

    @Data
    public static class Digest {
        /**
         * Cron expression for monthly digest schedule
         */
        private String schedule = "0 0 9 1 * ?";

        /**
         * Whether monthly digest is enabled
         */
        private boolean enabled = true;
    }
}
