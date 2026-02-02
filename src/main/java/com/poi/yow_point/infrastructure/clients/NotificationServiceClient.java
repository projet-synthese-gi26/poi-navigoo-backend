package com.poi.yow_point.infrastructure.clients;

import com.poi.yow_point.application.model.notification.NotificationResponse;
import com.poi.yow_point.application.model.notification.NotificationSendRequest;
import com.poi.yow_point.infrastructure.configuration.NotificationServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Reactive HTTP client for interacting with the Notification Service API.
 * Handles sending notifications via email, WhatsApp, SMS, etc.
 */
@Slf4j
@Component
public class NotificationServiceClient {

    private final WebClient webClient;
    private final String serviceToken;

    public NotificationServiceClient(NotificationServiceProperties properties) {
        this.serviceToken = properties.getService().getToken();
        this.webClient = WebClient.builder()
                .baseUrl(properties.getService().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Service-Token", serviceToken)
                .build();

        log.info("NotificationServiceClient initialized with base URL: {}", 
                properties.getService().getBaseUrl());
    }

    /**
     * Send a notification immediately via the Notification Service API.
     * 
     * @param request The notification request containing type, template ID, recipients, and data
     * @return Mono containing the response from the notification service
     */
    public Mono<NotificationResponse> sendNotification(NotificationSendRequest request) {
        log.info("Sending {} notification to {} using template ID: {}. Payload: {}", 
                request.getNotificationType(), 
                request.getTo(), 
                request.getTemplateId(),
                request);

        return webClient.post()
                .uri("/api/v1/notifications/send")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse -> 
                    clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("Notification API Error Details: status={}, body={}", 
                                    clientResponse.statusCode(), errorBody);
                            return Mono.error(new WebClientResponseException(
                                    clientResponse.statusCode().value(),
                                    clientResponse.statusCode().toString(),
                                    clientResponse.headers().asHttpHeaders(),
                                    errorBody != null ? errorBody.getBytes() : null,
                                    java.nio.charset.StandardCharsets.UTF_8
                            ));
                        })
                )
                .bodyToMono(NotificationResponse.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest))
                        .doBeforeRetry(retrySignal -> 
                                log.warn("Retrying notification send (attempt {}): {}", 
                                        retrySignal.totalRetries() + 1, 
                                        retrySignal.failure().getMessage())))
                .doOnSuccess(response -> {
                    if (response != null && response.getMessage() != null) {
                        log.info("Notification API response: {}", response.getMessage());
                    } else {
                        log.info("Notification API accepted the request successfully.");
                    }
                })
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException webClientError = (WebClientResponseException) error;
                        log.error("Notification API error: HTTP {} - Body: {}", 
                                webClientError.getStatusCode(), 
                                webClientError.getResponseBodyAsString());
                    } else {
                        log.error("Notification API error: {}", error.getMessage());
                    }
                });
    }

    /**
     * Send an email notification.
     * 
     * @param templateId Template ID for the email
     * @param to List of email addresses
     * @param data Template data variables
     * @return Mono containing the response
     */
    public Mono<NotificationResponse> sendEmail(Long templateId, String to, java.util.Map<String, Object> data) {
        NotificationSendRequest request = NotificationSendRequest.builder()
                .notificationType(NotificationSendRequest.NotificationType.EMAIL)
                .templateId(templateId)
                .to(java.util.List.of(to))
                .data(data)
                .build();
        
        return sendNotification(request);
    }

    /**
     * Send a WhatsApp notification.
     * 
     * @param templateId Template ID for the WhatsApp message
     * @param to Phone number (with country code)
     * @param data Template data variables
     * @return Mono containing the response
     */
    public Mono<NotificationResponse> sendWhatsApp(Long templateId, String to, java.util.Map<String, Object> data) {
        NotificationSendRequest request = NotificationSendRequest.builder()
                .notificationType(NotificationSendRequest.NotificationType.WHATSAPP)
                .templateId(templateId)
                .to(java.util.List.of(to))
                .data(data)
                .build();
        
        return sendNotification(request);
    }
}
