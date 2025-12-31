package com.poi.yow_point.infrastructure.repositories.poiReview;

import com.poi.yow_point.infrastructure.entities.PoiReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PoiReviewRepositoryCustom {
    Flux<PoiReview> findByPoiId(UUID poiId);

    Flux<PoiReview> findByUserId(UUID userId);

    Flux<PoiReview> findByOrganizationId(UUID organizationId);

    Flux<PoiReview> findByPlatformType(String platformType);

    Flux<PoiReview> findByPoiIdOrderByCreatedAtDesc(UUID poiId);

    Flux<PoiReview> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Mono<Double> findAverageRatingByPoiId(UUID poiId);

    Mono<Long> countByPoiId(UUID poiId);
}