package com.poi.yow_point.application.services.poiReview;

import com.poi.yow_point.presentation.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ReviewService {
    Mono<PoiReviewResponseDTO> createReviewForPoi(UUID poiId, ReviewRequestDTO reviewDTO);
    Mono<BlogReviewResponseDTO> createReviewForBlog(UUID blogId, ReviewRequestDTO reviewDTO);
    Mono<PodcastReviewResponseDTO> createReviewForPodcast(UUID podcastId, ReviewRequestDTO reviewDTO);

    Mono<Object> getReviewById(UUID reviewId); // Returns specific DTO or Generic

    Flux<PoiReviewResponseDTO> getReviewsByPoiId(UUID poiId);
    Flux<BlogReviewResponseDTO> getReviewsByBlogId(UUID blogId);
    Flux<PodcastReviewResponseDTO> getReviewsByPodcastId(UUID podcastId);

    Flux<Object> getReviewsByUserId(UUID userId); // Can return mixed types

    Mono<Object> updateReview(UUID reviewId, ReviewRequestDTO reviewDTO);

    Mono<Void> deleteReview(UUID reviewId);

    Mono<Double> getAverageRatingByPoiId(UUID poiId);

    Mono<Long> getReviewCountByPoiId(UUID poiId);

    Mono<Object> incrementLikes(UUID reviewId);

    Mono<Object> incrementDislikes(UUID reviewId);
}