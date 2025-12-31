package com.poi.yow_point.application.services.podcast;

import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.poi.yow_point.application.mappers.PodcastMapper;
import com.poi.yow_point.application.validation.PodcastValidator;
import com.poi.yow_point.infrastructure.repositories.podcast.PodcastRepository;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastDTO;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PodcastServiceImpl implements PodcastService {

    private final PodcastRepository podcastRepository;
    private final PodcastMapper podcastMapper;
    private final R2dbcEntityTemplate entityTemplate;
    private final PodcastValidator podcastValidator;

    public PodcastServiceImpl(PodcastRepository podcastRepository, PodcastMapper podcastMapper,
            R2dbcEntityTemplate entityTemplate,
            PodcastValidator podcastValidator) {
        this.podcastRepository = podcastRepository;
        this.podcastMapper = podcastMapper;
        this.podcastValidator = podcastValidator;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<PodcastDTO> createPodcast(PodcastCreateRequest podcastCreateDto) {
        return podcastValidator.validateCreateDto(podcastCreateDto)
                .then(Mono.fromCallable(() -> podcastMapper.toEntity(podcastCreateDto)))
                .flatMap(podcast -> entityTemplate.insert(podcast))
                .map(podcastMapper::toDto);
    }

    @Override
    public Mono<PodcastDTO> updatePodcast(UUID podcastId, UpdatePodcastRequest podcastUpdateDto) {
        return podcastValidator.validateUpdateDto(podcastUpdateDto)
                .then(podcastRepository.findById(podcastId))
                .switchIfEmpty(Mono.error(new RuntimeException("Podcast not found with id: " + podcastId)))
                .doOnNext(podcast -> podcastMapper.updateEntityFromDto(podcastUpdateDto, podcast))
                .flatMap(podcastRepository::save)
                .map(podcastMapper::toDto);
    }

    @Override
    public Mono<PodcastDTO> getPodcastById(UUID podcastId) {
        return podcastRepository.findByIdAndIsActiveTrue(podcastId)
                .switchIfEmpty(Mono.error(new RuntimeException("Podcast not found with id: " + podcastId)))
                .map(podcastMapper::toDto);
    }

    @Override
    public Flux<PodcastDTO> getAllPodcasts() {
        return podcastRepository.findAllActivePodcasts()
                .map(podcastMapper::toDto);
    }

    @Override
    public Flux<PodcastDTO> getPodcastsByUserId(UUID userId) {
        return podcastRepository.findByUserIdAndIsActiveTrue(userId)
                .map(podcastMapper::toDto);
    }

    @Override
    public Flux<PodcastDTO> getPodcastsByPoiId(UUID poiId) {
        return podcastRepository.findByPoiIdAndIsActiveTrue(poiId)
                .map(podcastMapper::toDto);
    }

    @Override
    public Flux<PodcastDTO> searchPodcastsByTitle(String title) {
        return podcastRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue("%" + title + "%")
                .map(podcastMapper::toDto);
    }

    @Override
    public Flux<PodcastDTO> getPodcastsByDurationRange(Integer minDuration, Integer maxDuration) {
        return podcastRepository.findByDurationRange(minDuration, maxDuration)
                .map(podcastMapper::toDto);
    }

    @Override
    public Mono<Void> deletePodcast(UUID podcastId) {
        return podcastRepository.findById(podcastId)
                .switchIfEmpty(Mono.error(new RuntimeException("Podcast not found with id: " + podcastId)))
                .doOnNext(podcast -> podcast.setIsActive(false))
                .flatMap(podcastRepository::save)
                .then();
    }

    @Override
    public Mono<Long> countPodcastsByUserId(UUID userId) {
        return podcastRepository.countByUserIdAndIsActiveTrue(userId);
    }

    @Override
    public Mono<Long> countPodcastsByPoiId(UUID poiId) {
        return podcastRepository.countByPoiIdAndIsActiveTrue(poiId);
    }
}