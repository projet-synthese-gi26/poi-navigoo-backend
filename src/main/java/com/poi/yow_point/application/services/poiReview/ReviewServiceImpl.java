package com.poi.yow_point.application.services.poiReview;

import com.poi.yow_point.application.services.websocket.PoiEventPublisher;
import com.poi.yow_point.infrastructure.entities.Review;
import com.poi.yow_point.infrastructure.repositories.poiReview.ReviewRepository;
import com.poi.yow_point.presentation.dto.*;
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
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final R2dbcEntityTemplate entityTemplate;
    private final PopularityScoreService popularityScoreService;

    @Override
    public Mono<PoiReviewResponseDTO> createReviewForPoi(UUID poiId, ReviewRequestDTO reviewDTO) {
        log.info("Creating review for POI: {}", poiId);
        return createReviewEntity(reviewDTO)
                .map(review -> {
                    review.setPoiId(poiId);
                    return review;
                })
                .flatMap(reviewRepository::save)
                .flatMap(savedReview -> popularityScoreService.updatePoiPopularityScore(poiId)
                        .thenReturn(toPoiReviewResponseDTO(savedReview)));
    }

    @Override
    public Mono<BlogReviewResponseDTO> createReviewForBlog(UUID blogId, ReviewRequestDTO reviewDTO) {
        log.info("Creating review for Blog: {}", blogId);
        return createReviewEntity(reviewDTO)
                .map(review -> {
                    review.setBlogId(blogId);
                    return review;
                })
                .flatMap(reviewRepository::save)
                .map(this::toBlogReviewResponseDTO);
    }

    @Override
    public Mono<PodcastReviewResponseDTO> createReviewForPodcast(UUID podcastId, ReviewRequestDTO reviewDTO) {
        log.info("Creating review for Podcast: {}", podcastId);
        return createReviewEntity(reviewDTO)
                .map(review -> {
                    review.setPodcastId(podcastId);
                    return review;
                })
                .flatMap(reviewRepository::save)
                .map(this::toPodcastReviewResponseDTO);
    }

    private Mono<Review> createReviewEntity(ReviewRequestDTO dto) {
        return Mono.fromCallable(() -> Review.builder()
                .reviewId(UUID.randomUUID())
                .isNew(true)
                .userId(dto.getUserId())
                .platformType(dto.getPlatformType())
                .rating(dto.getRating())
                .reviewText(dto.getReviewText())
                .createdAt(OffsetDateTime.now())
                .likes(dto.getLikes())
                .dislikes(dto.getDislikes())
                .build());
    }

    @Override
    public Mono<Object> getReviewById(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .map(this::toResponseDTO);
    }

    @Override
    public Flux<PoiReviewResponseDTO> getReviewsByPoiId(UUID poiId) {
        return reviewRepository.findByPoiIdOrderByCreatedAtDesc(poiId)
                .map(this::toPoiReviewResponseDTO);
    }

    @Override
    public Flux<BlogReviewResponseDTO> getReviewsByBlogId(UUID blogId) {
        return reviewRepository.findByBlogId(blogId)
                .map(this::toBlogReviewResponseDTO);
    }

    @Override
    public Flux<PodcastReviewResponseDTO> getReviewsByPodcastId(UUID podcastId) {
        return reviewRepository.findByPodcastId(podcastId)
                .map(this::toPodcastReviewResponseDTO);
    }

    @Override
    public Flux<Object> getReviewsByUserId(UUID userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public Mono<Object> updateReview(UUID reviewId, ReviewRequestDTO reviewDTO) {
        return reviewRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new RuntimeException("Review not found")))
                .map(review -> {
                    if (reviewDTO.getRating() != null) review.setRating(reviewDTO.getRating());
                    if (reviewDTO.getReviewText() != null) review.setReviewText(reviewDTO.getReviewText());
                    if (reviewDTO.getLikes() != null) review.setLikes(reviewDTO.getLikes());
                    if (reviewDTO.getDislikes() != null) review.setDislikes(reviewDTO.getDislikes());
                    return review;
                })
                .flatMap(reviewRepository::save)
                .flatMap(savedReview -> {
                    if (savedReview.getPoiId() != null) {
                        return popularityScoreService.updatePoiPopularityScore(savedReview.getPoiId())
                                .thenReturn(toResponseDTO(savedReview));
                    }
                    return Mono.just(toResponseDTO(savedReview));
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> {
                    UUID poiId = review.getPoiId();
                    return reviewRepository.deleteById(reviewId)
                            .then(Mono.defer(() -> poiId != null ? 
                                    popularityScoreService.updatePoiPopularityScore(poiId) : Mono.empty()));
                });
    }

    @Override
    public Mono<Double> getAverageRatingByPoiId(UUID poiId) {
        return reviewRepository.findAverageRatingByPoiId(poiId);
    }

    @Override
    public Mono<Long> getReviewCountByPoiId(UUID poiId) {
        return reviewRepository.countByPoiId(poiId);
    }

    @Override
    public Mono<Object> incrementLikes(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> {
                    review.setLikes(review.getLikes() + 1);
                    return reviewRepository.save(review);
                })
                .map(this::toResponseDTO);
    }

    @Override
    public Mono<Object> incrementDislikes(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> {
                    review.setDislikes(review.getDislikes() + 1);
                    return reviewRepository.save(review);
                })
                .map(this::toResponseDTO);
    }

    private PoiReviewResponseDTO toPoiReviewResponseDTO(Review review) {
        return PoiReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .poiId(review.getPoiId())
                .userId(review.getUserId())
                .platformType(review.getPlatformType())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .likes(review.getLikes())
                .dislikes(review.getDislikes())
                .build();
    }

    private BlogReviewResponseDTO toBlogReviewResponseDTO(Review review) {
        return BlogReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .blogId(review.getBlogId())
                .userId(review.getUserId())
                .platformType(review.getPlatformType())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .likes(review.getLikes())
                .dislikes(review.getDislikes())
                .build();
    }

    private PodcastReviewResponseDTO toPodcastReviewResponseDTO(Review review) {
        return PodcastReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .podcastId(review.getPodcastId())
                .userId(review.getUserId())
                .platformType(review.getPlatformType())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .likes(review.getLikes())
                .dislikes(review.getDislikes())
                .build();
    }

    private Object toResponseDTO(Review review) {
        if (review.getPoiId() != null) return toPoiReviewResponseDTO(review);
        if (review.getBlogId() != null) return toBlogReviewResponseDTO(review);
        if (review.getPodcastId() != null) return toPodcastReviewResponseDTO(review);
        return null; // Should not happen given constraints
    }
}