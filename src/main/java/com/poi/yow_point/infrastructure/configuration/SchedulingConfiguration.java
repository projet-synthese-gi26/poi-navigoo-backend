package com.poi.yow_point.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable Spring's scheduled task execution capability.
 * This allows the use of @Scheduled annotations for periodic tasks like monthly digests.
 */
@Configuration
@EnableScheduling
public class SchedulingConfiguration {
    // Spring will automatically detect @Scheduled methods in beans
}
