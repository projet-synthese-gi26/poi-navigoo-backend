package com.poi.yow_point.application.services.poiReview;

import com.poi.yow_point.application.mappers.PoiReviewMapper;
import com.poi.yow_point.application.services.websocket.PoiEventPublisher;
import com.poi.yow_point.infrastructure.entities.PoiReview;
import com.poi.yow_point.infrastructure.repositories.poiReview.PoiReviewRepository;
import com.poi.yow_point.presentation.dto.PoiReviewDTO;
import com.poi.yow_point.application.validation.PoiReviewValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoiReviewServiceImpl implements PoiReviewService {

    private final PoiReviewRepository poiReviewRepository;
    private final PoiReviewMapper poiReviewMapper;
    private final PoiReviewValidator poiReviewValidator;
    private final R2dbcEntityTemplate entityTemplate;
    private final PoiEventPublisher eventPublisher;

    @Override
    public Mono<PoiReviewDTO> createReview(PoiReviewDTO reviewDTO) {
        log.info("Creating review for POI: {}", reviewDTO.getPoiId());

        return poiReviewValidator.validateCreateReview(reviewDTO)
                .then(Mono.fromCallable(() -> {
                    PoiReview review = poiReviewMapper.toEntity(reviewDTO);
                    review.setReviewId(UUID.randomUUID());
                    review.setCreatedAt(OffsetDateTime.now());
                    return review;
                }))
                .flatMap(entityTemplate::insert)
                .map(poiReviewMapper::toDTO)
                .doOnSuccess(savedReview -> log.info("Review created with ID: {}", savedReview.getReviewId()))
                .doOnError(error -> log.error("Error creating review: {}", error.getMessage(), error));
    }

    @Override
    public Mono<PoiReviewDTO> getReviewById(UUID reviewId) {
        log.info("Fetching review with ID: {}", reviewId);

        return poiReviewValidator.validateUUID(reviewId, "Review ID")
                .then(poiReviewRepository.findById(reviewId))
                .map(poiReviewMapper::toDTO)
                .doOnSuccess(review -> log.info("Review found: {}", reviewId))
                .switchIfEmpty(Mono.error(new RuntimeException("Review not found with ID: " + reviewId)));
    }

    @Override
    public Flux<PoiReviewDTO> getAllReviews() {
        log.info("Fetching all reviews");

        return poiReviewRepository.findAll()
                .map(poiReviewMapper::toDTO)
                .doOnComplete(() -> log.info("All reviews fetched"));
    }

    @Override
    public Flux<PoiReviewDTO> getReviewsByPoiId(UUID poiId) {
        log.info("Fetching reviews for POI: {}", poiId);

        return poiReviewValidator.validateUUID(poiId, "POI ID")
                .thenMany(poiReviewRepository.findByPoiIdOrderByCreatedAtDesc(poiId))
                .map(poiReviewMapper::toDTO)
                .doOnComplete(() -> log.info("Reviews fetched for POI: {}", poiId));
    }

    @Override
    public Flux<PoiReviewDTO> getReviewsByUserId(UUID userId) {
        log.info("Fetching reviews for user: {}", userId);

        return poiReviewValidator.validateUUID(userId, "User ID")
                .thenMany(poiReviewRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .map(poiReviewMapper::toDTO)
                .doOnComplete(() -> log.info("Reviews fetched for user: {}", userId));
    }

    @Override
    public Flux<PoiReviewDTO> getReviewsByOrganizationId(UUID organizationId) {
        log.info("Fetching reviews for organization: {}", organizationId);

        return poiReviewValidator.validateUUID(organizationId, "Organization ID")
                .thenMany(poiReviewRepository.findByOrganizationId(organizationId))
                .map(poiReviewMapper::toDTO)
                .doOnComplete(() -> log.info("Reviews fetched for organization: {}", organizationId));
    }

    @Override
    @Transactional
    public Mono<PoiReviewDTO> updateReview(UUID reviewId, PoiReviewDTO reviewDTO) {
        log.info("Updating review with ID: {}", reviewId);

        return poiReviewValidator.validateUUID(reviewId, "Review ID")
                .then(poiReviewValidator.validateUpdateReview(reviewDTO))
                .then(poiReviewRepository.findById(reviewId))
                .switchIfEmpty(Mono.error(new RuntimeException("Review not found with ID: " + reviewId)))
                .map(existingReview -> {
                    if (reviewDTO.getRating() != null) {
                        existingReview.setRating(reviewDTO.getRating());
                    }
                    if (reviewDTO.getReviewText() != null) {
                        existingReview.setReviewText(reviewDTO.getReviewText());
                    }
                    if (reviewDTO.getLikes() != null) {
                        existingReview.setLikes(reviewDTO.getLikes());
                    }
                    if (reviewDTO.getDislikes() != null) {
                        existingReview.setDislikes(reviewDTO.getDislikes());
                    }
                    return existingReview;
                })
                .flatMap(poiReviewRepository::save)
                .map(poiReviewMapper::toDTO)
                .doOnSuccess(updatedReview -> log.info("Review updated: {}", reviewId))
                .doOnError(error -> log.error("Error updating review {}: {}", reviewId, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteReview(UUID reviewId) {
        log.info("Deleting review with ID: {}", reviewId);

        return poiReviewValidator.validateUUID(reviewId, "Review ID")
                .then(poiReviewRepository.existsById(reviewId))
                .flatMap(exists -> {
                    if (exists) {
                        return poiReviewRepository.deleteById(reviewId);
                    } else {
                        return Mono.error(new RuntimeException("Review not found with ID: " + reviewId));
                    }
                })
                .doOnSuccess(unused -> log.info("Review deleted: {}", reviewId))
                .doOnError(error -> log.error("Error deleting review {}: {}", reviewId, error.getMessage()));
    }

    @Override
    public Mono<Double> getAverageRatingByPoiId(UUID poiId) {
        log.info("Calculating average rating for POI: {}", poiId);

        return poiReviewValidator.validateUUID(poiId, "POI ID")
                .then(poiReviewRepository.findAverageRatingByPoiId(poiId))
                .doOnSuccess(avgRating -> log.info("Average rating for POI {}: {}", poiId, avgRating));
    }

    @Override
    public Mono<Long> getReviewCountByPoiId(UUID poiId) {
        log.info("Counting reviews for POI: {}", poiId);

        return poiReviewValidator.validateUUID(poiId, "POI ID")
                .then(poiReviewRepository.countByPoiId(poiId))
                .doOnSuccess(count -> log.info("Review count for POI {}: {}", poiId, count));
    }

    @Override
    public Mono<PoiReviewDTO> incrementLikes(UUID reviewId) {
        log.info("Incrementing likes for review: {}", reviewId);

        return poiReviewValidator.validateUUID(reviewId, "Review ID")
                .then(poiReviewRepository.findById(reviewId))
                .switchIfEmpty(Mono.error(new RuntimeException("Review not found with ID: " + reviewId)))
                .map(review -> {
                    review.setLikes(review.getLikes() + 1);
                    return review;
                })
                .flatMap(poiReviewRepository::save)
                .map(poiReviewMapper::toDTO)
                .doOnSuccess(review -> log.info("Likes incremented for review: {}", reviewId));
    }

    @Override
    public Mono<PoiReviewDTO> incrementDislikes(UUID reviewId) {
        log.info("Incrementing dislikes for review: {}", reviewId);

        return poiReviewValidator.validateUUID(reviewId, "Review ID")
                .then(poiReviewRepository.findById(reviewId))
                .switchIfEmpty(Mono.error(new RuntimeException("Review not found with ID: " + reviewId)))
                .map(review -> {
                    review.setDislikes(review.getDislikes() + 1);
                    return review;
                })
                .flatMap(poiReviewRepository::save)
                .map(poiReviewMapper::toDTO)
                .doOnSuccess(review -> log.info("Dislikes incremented for review: {}", reviewId));
    }
}