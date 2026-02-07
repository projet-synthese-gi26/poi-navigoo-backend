package com.poi.yow_point.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    
    /**
     * Maximum number of failed login attempts before account lockout
     */
    private Integer maxFailedAttempts = 5;
    
    /**
     * Account lockout duration in minutes
     */
    private Integer lockoutDurationMinutes = 15;
}
