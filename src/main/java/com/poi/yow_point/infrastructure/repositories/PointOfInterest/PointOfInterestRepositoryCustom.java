package com.poi.yow_point.infrastructure.repositories.PointOfInterest;

import java.util.UUID;

import com.poi.yow_point.infrastructure.entities.PointOfInterest;

import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
import reactor.core.publisher.Mono;

public interface PointOfInterestRepositoryCustom {

    Flux<PointOfInterest> findByLocationWithinRadius(Double latitude, Double longitude, Double radiusKm);

    Flux<PointOfInterest> findTopByPopularityScore(Integer limit);

    Mono<Boolean> existsByNameAndOrganizationIdExcludingId(String name, UUID organizationId, UUID excludeId);

    Mono<Long> deactivateById(UUID poiId);

    Mono<Long> activateById(UUID poiId);

    Mono<Long> updatePopularityScore(UUID poiId, Float score);

    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    Flux<PointOfInterest> findRecent(Integer limit);

}
