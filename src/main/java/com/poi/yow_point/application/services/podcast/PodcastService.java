package com.poi.yow_point.application.services.podcast;

import java.util.UUID;

import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastDTO;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PodcastService {
    Mono<PodcastDTO> createPodcast(PodcastCreateRequest podcastCreateDto);

    Mono<PodcastDTO> updatePodcast(UUID podcastId, UpdatePodcastRequest podcastUpdateDto);

    Mono<PodcastDTO> getPodcastById(UUID podcastId);

    Flux<PodcastDTO> getAllPodcasts();

    Flux<PodcastDTO> getPodcastsByUserId(UUID userId);

    Flux<PodcastDTO> getPodcastsByPoiId(UUID poiId);

    Flux<PodcastDTO> searchPodcastsByTitle(String title);

    Flux<PodcastDTO> getPodcastsByDurationRange(Integer minDuration, Integer maxDuration);

    Mono<Void> deletePodcast(UUID podcastId);

    Mono<Long> countPodcastsByUserId(UUID userId);

    Mono<Long> countPodcastsByPoiId(UUID poiId);
}
