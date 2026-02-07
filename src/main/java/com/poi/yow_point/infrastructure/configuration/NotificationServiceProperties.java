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
        private EmailTemplate email = new EmailTemplate();

        /**
         * Template ID for monthly digest email
         */
        private Long monthlyDigestEmail = 11L;

        /**
         * Template ID for user registration email
         */
        private Long userRegistration;

        /**
         * Template ID for password reset email
         */
        private Long passwordReset;

        /**
         * Template ID for password verification email
         */
        private Long passwordVerification;

        @Data
        public static class EmailTemplate {
            /**
             * Template ID for POI submission email
             */
            private Long poiSubmitted = 18L;

            /**
             * Template ID for POI approval email
             */
            private Long poiApproved = 19L;

            /**
             * Template ID for POI rejection email
             */
            private Long poiRejected = 20L;
        }

        /**
         * Get template ID by name
         */
        public Long getTemplateId(String templateName) {
            return switch (templateName) {
                case "user-registration" -> userRegistration;
                case "password-reset" -> passwordReset;
                case "password-verification" -> passwordVerification;
                case "poi-submitted" -> email.getPoiSubmitted();
                case "poi-approved" -> email.getPoiApproved();
                case "poi-rejected" -> email.getPoiRejected();
                case "monthly-digest-email" -> monthlyDigestEmail;
                default -> null;
            };
        }
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
