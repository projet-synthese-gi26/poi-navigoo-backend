package com.poi.yow_point.infrastructure.repositories.podcast;

import java.util.UUID;

import com.poi.yow_point.infrastructure.entities.Podcast;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PodcastRepositoryCustom {

    Flux<Podcast> findByUserIdAndIsActiveTrue(UUID userId);

    Flux<Podcast> findByPoiIdAndIsActiveTrue(UUID poiId);

    Flux<Podcast> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    Flux<Podcast> findAllActivePodcasts();

    Mono<Podcast> findByIdAndIsActiveTrue(UUID podcastId);

    Mono<Long> countByUserIdAndIsActiveTrue(UUID userId);

    Mono<Long> countByPoiIdAndIsActiveTrue(UUID poiId);

    Flux<Podcast> findByDurationRange(Integer minDuration, Integer maxDuration);
}