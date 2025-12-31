package com.poi.yow_point.application.services.poiReview;

import com.poi.yow_point.presentation.dto.PoiReviewDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface PoiReviewService {
    Mono<PoiReviewDTO> createReview(PoiReviewDTO reviewDTO);

    Mono<PoiReviewDTO> getReviewById(UUID reviewId);

    Flux<PoiReviewDTO> getAllReviews();

    Flux<PoiReviewDTO> getReviewsByPoiId(UUID poiId);

    Flux<PoiReviewDTO> getReviewsByUserId(UUID userId);

    Flux<PoiReviewDTO> getReviewsByOrganizationId(UUID organizationId);

    Mono<PoiReviewDTO> updateReview(UUID reviewId, PoiReviewDTO reviewDTO);

    Mono<Void> deleteReview(UUID reviewId);

    Mono<Double> getAverageRatingByPoiId(UUID poiId);

    Mono<Long> getReviewCountByPoiId(UUID poiId);

    Mono<PoiReviewDTO> incrementLikes(UUID reviewId);

    Mono<PoiReviewDTO> incrementDislikes(UUID reviewId);
}