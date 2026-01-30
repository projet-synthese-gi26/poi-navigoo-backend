package com.poi.yow_point.application.services.point_of_interest;

import com.poi.yow_point.presentation.dto.PointOfInterestDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PointOfInterestService {
    Mono<PointOfInterestDTO> createPoi(PointOfInterestDTO dto);

    Mono<PointOfInterestDTO> updatePoi(UUID poiId, PointOfInterestDTO dto);

    Mono<PointOfInterestDTO> findById(UUID poiId);

    Flux<PointOfInterestDTO> findActiveByOrganizationId(UUID organizationId);

    Flux<PointOfInterestDTO> findByOrganizationId(UUID organizationId);

    Flux<PointOfInterestDTO> findByLocationWithinRadius(Double latitude, Double longitude, Double radiusKm);

    Flux<PointOfInterestDTO> findByType(com.poi.yow_point.application.model.PoiType poiType);

    Flux<PointOfInterestDTO> findByCategory(com.poi.yow_point.application.model.PoiCategory poiCategory);

    Flux<PointOfInterestDTO> searchByName(String name);

    Flux<PointOfInterestDTO> findTopPopular(Integer limit);

    Mono<Void> deactivatePoi(UUID poiId);

    Mono<Void> activatePoi(UUID poiId);

    Mono<Void> deletePoi(UUID poiId);

    Mono<Void> updatePopularityScore(UUID poiId, Float score);

    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    Flux<PointOfInterestDTO> findByCreatedByUserId(UUID userId);

    Flux<PointOfInterestDTO> findByCity(String city);

    Mono<Boolean> existsByNameAndOrganization(String name, UUID organizationId, UUID excludeId);

    Flux<PointOfInterestDTO> findAll();
}