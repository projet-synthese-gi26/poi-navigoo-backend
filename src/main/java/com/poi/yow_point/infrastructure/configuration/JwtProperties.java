package com.poi.yow_point.infrastructure.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
    /**
     * Secret key for signing JWT tokens
     */
    private String secret;
    
    /**
     * Access token expiration time in milliseconds (default: 15 minutes)
     */
    private Long accessTokenExpiration = 900000L;
    
    /**
     * Refresh token expiration time in milliseconds (default: 7 days)
     */
    private Long refreshTokenExpiration = 604800000L;
}
