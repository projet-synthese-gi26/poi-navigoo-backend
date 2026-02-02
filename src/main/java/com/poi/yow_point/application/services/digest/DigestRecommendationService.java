package com.poi.yow_point.application.services.digest;

import com.poi.yow_point.application.services.point_of_interest.PointOfInterestService;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating personalized POI recommendations for users.
 * Used for monthly digest emails.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DigestRecommendationService {

    private final PointOfInterestService poiService;

    /**
     * Generate POI recommendations for a user based on their organization.
     * Returns top 10 popular POIs from the user's organization.
     * 
     * @param organizationId The user's organization ID
     * @return Flux of recommended POIs
     */
    public Flux<PointOfInterestDTO> generateRecommendations(UUID organizationId) {
        log.debug("Generating POI recommendations for organization: {}", organizationId);

        // Get active POIs from the user's organization
        return poiService.findActiveByOrganizationId(organizationId)
                .sort(Comparator.comparing(
                        poi -> poi.getPopularityScore() != null ? poi.getPopularityScore() : 0.0f,
                        Comparator.reverseOrder()
                ))
                .take(10) // Limit to top 10 recommendations
                .doOnComplete(() -> log.debug("Generated recommendations for organization: {}", organizationId));
    }

    /**
     * Generate POI recommendations for a user.
     * This version can be extended to include more personalization logic.
     * 
     * @param userId The user ID
     * @param organizationId The user's organization ID
     * @return List of recommended POIs
     */
    public Flux<PointOfInterestDTO> generatePersonalizedRecommendations(UUID userId, UUID organizationId) {
        log.debug("Generating personalized POI recommendations for user: {}", userId);

        // For now, use organization-based recommendations
        // This can be extended to include:
        // - POIs in the user's city
        // - POIs the user has interacted with
        // - Recently created POIs
        // - POIs matching user's interests
        
        return generateRecommendations(organizationId);
    }
}
