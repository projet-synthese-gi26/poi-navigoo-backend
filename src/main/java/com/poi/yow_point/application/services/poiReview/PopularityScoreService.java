package com.poi.yow_point.application.services.poiReview;

import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.infrastructure.entities.Review;
import com.poi.yow_point.infrastructure.repositories.PointOfInterest.PointOfInterestRepository;
import com.poi.yow_point.infrastructure.repositories.poiReview.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularityScoreService {

    private final ReviewRepository reviewRepository;
    private final PointOfInterestRepository poiRepository;

    // lambda for half-life of 180 days: ln(2) / 180
    private static final double LAMBDA = 0.00385;
    private static final double M_THRESHOLD = 5.0;

    /**
     * Recalculate and update the popularity score for a specific POI.
     */
    @Transactional
    public Mono<Void> updatePoiPopularityScore(UUID poiId) {
        log.info("Recalculating popularity score for POI: {}", poiId);

        return Mono.zip(
                reviewRepository.findByPoiId(poiId).collectList(),
                reviewRepository.findGlobalAverageRating(),
                reviewRepository.countTotalReviews()
        ).flatMap(tuple -> {
            var reviews = tuple.getT1();
            double globalAvg = tuple.getT2();
            
            if (reviews.isEmpty()) {
                return poiRepository.findById(poiId)
                        .flatMap(poi -> {
                            poi.setPopularityScore((float) globalAvg);
                            return poiRepository.save(poi);
                        }).then();
            }

            // Calculate freshness-weighted sum of ratings and sum of weights
            double weightedSum = 0;
            double totalWeight = 0;
            Instant now = Instant.now();

            for (Review review : reviews) {
                OffsetDateTime reviewDate = review.getCreatedAt();
                long days = Duration.between(reviewDate.toInstant(), now).toDays();
                double weight = Math.exp(-LAMBDA * days);
                
                weightedSum += review.getRating() * weight;
                totalWeight += weight;
            }

            double averageRating = weightedSum / totalWeight;
            
            // Bayesian Average formula: (v / (v + m)) * R + (m / (v + m)) * C
            double score = (totalWeight / (totalWeight + M_THRESHOLD)) * averageRating +
                           (M_THRESHOLD / (totalWeight + M_THRESHOLD)) * globalAvg;

            return poiRepository.findById(poiId)
                    .flatMap(poi -> {
                        poi.setPopularityScore((float) score);
                        return poiRepository.save(poi);
                    }).then();
        });
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void updateAllPoiPopularityScores() {
        log.info("Triggering batch update for all POI popularity scores");
        poiRepository.findAll()
                .flatMap(poi -> updatePoiPopularityScore(poi.getPoiId()))
                .then()
                .subscribe();
    }
}
