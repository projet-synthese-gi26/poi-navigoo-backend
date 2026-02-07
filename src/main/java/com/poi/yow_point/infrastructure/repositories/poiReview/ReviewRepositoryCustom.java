package com.poi.yow_point.infrastructure.repositories.poiReview;

import com.poi.yow_point.infrastructure.entities.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReviewRepositoryCustom {
    Flux<Review> findByPoiId(UUID poiId);

    Flux<Review> findByUserId(UUID userId);

    // Removed findByOrganizationId as review no longer has organization_id

    Flux<Review> findByPlatformType(String platformType);

    Flux<Review> findByPoiIdOrderByCreatedAtDesc(UUID poiId);

    Flux<Review> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Mono<Double> findAverageRatingByPoiId(UUID poiId);

    Mono<Long> countByPoiId(UUID poiId);

    Mono<Double> findGlobalAverageRating();

    Mono<Long> countTotalReviews();
}