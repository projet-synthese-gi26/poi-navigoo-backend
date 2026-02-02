package com.poi.yow_point.application.services.point_of_interest;

import com.poi.yow_point.application.mappers.MapperUtils;
import com.poi.yow_point.application.mappers.PointOfInterestMapper;
import com.poi.yow_point.application.exceptions.ResourceNotFoundException;
import com.poi.yow_point.application.services.appUser.AppUserService;
import com.poi.yow_point.application.services.notification.NotificationService;
import com.poi.yow_point.application.services.websocket.PoiEventPublisher;
import com.poi.yow_point.application.validation.PointOfInterestValidator;
import com.poi.yow_point.infrastructure.kafka.KafkaProducerService;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.websocketDTO.PoiEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointOfInterestServiceImpl implements PointOfInterestService {

    private final com.poi.yow_point.infrastructure.repositories.PointOfInterest.PointOfInterestRepository repository;
    private final PointOfInterestMapper mapper;
    private final MapperUtils mapperUtils;
    private final PointOfInterestValidator validator;
    private final PoiEventPublisher eventPublisher;
    private final KafkaProducerService kafkaProducerService;
    private final ReactiveRedisTemplate<String, PointOfInterestDTO> redisTemplate;
    private final NotificationService notificationService;
    private final AppUserService appUserService;

    private static final String CACHE_KEY_PREFIX = "poi:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    @Override
    @Transactional
    public Mono<PointOfInterestDTO> createPoi(PointOfInterestDTO dto) {
        return validateDto(dto)
                .flatMap(validatedDto -> repository.existsByNameAndOrganizationIdExcludingId(
                        validatedDto.getPoiName(),
                        validatedDto.getOrganizationId(),
                        null)
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException(
                                        "A POI with this name already exists in the organization"));
                            }
                            return Mono.just(validatedDto);
                        }))
                .map(validatedDto -> mapper.toEntity(validatedDto, mapperUtils))
                .doOnNext(entity -> {
                    Instant now = Instant.now();
                    entity.setCreatedAt(now);
                    entity.setUpdatedAt(now);
                    if (entity.getIsActive() == null)
                        entity.setIsActive(true);
                    if (entity.getPopularityScore() == null)
                        entity.setPopularityScore(0.0f);
                })
                .flatMap(repository::save)
                .map(mapper::toDto)
                .doOnSuccess(savedDto -> {
                    log.info("POI created successfully with ID: {}. Publishing WebSocket event...",
                            savedDto.getPoiId());
                    eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_CREATED, savedDto));
                    kafkaProducerService.sendMessage("poi-created", savedDto);
                    
                    // Send notifications asynchronously (non-blocking)
                    if (savedDto.getCreatedByUserId() != null) {
                        appUserService.getUserById(savedDto.getCreatedByUserId())
                                .flatMap(userDto -> notificationService.notifyPoiCreated(savedDto, userDto))
                                .onErrorResume(error -> {
                                    log.error("Failed to send POI creation notifications: {}", error.getMessage());
                                    return Mono.empty();
                                })
                                .subscribe(); // Fire and forget
                    }
                })
                .doOnError(error -> log.error("Error creating POI: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<PointOfInterestDTO> updatePoi(UUID poiId, PointOfInterestDTO dto) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable (DELETE) for POI {}: {}", poiId, e.getMessage());
                    return Mono.just(true); // Continue anyway
                })
                .then(validateDto(dto))
                .flatMap(validatedDto -> repository.findById(poiId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("POI not found with ID: " + poiId)))
                .flatMap(existingEntity -> {
                    if (dto.getPoiName() != null && !dto.getPoiName().equals(existingEntity.getPoiName())) {
                        return repository.existsByNameAndOrganizationIdExcludingId(
                                dto.getPoiName(),
                                existingEntity.getOrganizationId(),
                                poiId)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new IllegalArgumentException(
                                                "A POI with this name already exists in the organization"));
                                    }
                                    return Mono.just(existingEntity);
                                });
                    }
                    return Mono.just(existingEntity);
                })
                .map(existingEntity -> {
                    mapper.updateEntityFromDto(existingEntity, dto, mapperUtils);
                    existingEntity.setUpdatedAt(java.time.Instant.now());
                    return existingEntity;
                })
                .flatMap(repository::save)
                .map(mapper::toDto)
                .doOnSuccess(updatedDto -> {
                    log.info("POI updated successfully: {},  Publishing WebSocket event...",
                            updatedDto.getPoiId());
                    eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_UPDATED, updatedDto));
                    kafkaProducerService.sendMessage("poi-updated", updatedDto);
                    
                    // Send notifications if updater is different from creator (non-blocking)
                    if (updatedDto.getCreatedByUserId() != null && updatedDto.getUpdatedByUserId() != null
                            && !updatedDto.getCreatedByUserId().equals(updatedDto.getUpdatedByUserId())) {
                        
                        Mono.zip(
                                appUserService.getUserById(updatedDto.getCreatedByUserId()),
                                appUserService.getUserById(updatedDto.getUpdatedByUserId())
                        )
                        .flatMap(tuple -> notificationService.notifyPoiUpdated(
                                updatedDto, tuple.getT1(), tuple.getT2()))
                        .onErrorResume(error -> {
                            log.error("Failed to send POI update notifications: {}", error.getMessage());
                            return Mono.empty();
                        })
                        .subscribe(); // Fire and forget
                    }
                })
                .doOnError(error -> log.error("Error updating POI {}: {}", poiId, error.getMessage()));
    }

    @Override
    public Mono<PointOfInterestDTO> findById(UUID poiId) {
        String cacheKey = CACHE_KEY_PREFIX + poiId;

        return redisTemplate.opsForValue()
                .get(cacheKey)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable (GET), falling back to DB for POI {}: {}", poiId, e.getMessage());
                    return Mono.empty(); // 🔥 tolérance Redis
                })
                .doOnNext(dto -> log.debug("Cache hit for POI: {}", poiId))
                .switchIfEmpty(
                        repository.findById(poiId)
                                .map(mapper::toDto)
                                .flatMap(dto ->
                                        redisTemplate.opsForValue()
                                                .set(cacheKey, dto, CACHE_TTL)
                                                .onErrorResume(e -> {
                                                    log.warn("Redis unavailable (SET) for POI {}: {}", poiId, e.getMessage());
                                                    return Mono.empty(); // 🔥 Redis non bloquant
                                                })
                                                .thenReturn(dto)
                                )
                                .doOnNext(dto ->
                                        log.debug("Cache miss for POI: {}, saved to cache with TTL {}", poiId, CACHE_TTL)
                                )
                )
                .doOnError(error ->
                        log.error("Error finding POI {} (DB level): {}", poiId, error.getMessage())
                );
    }


    @Override
    public Flux<PointOfInterestDTO> findActiveByOrganizationId(UUID organizationId) {
        return repository.findActiveByOrganizationId(organizationId)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved active POIs for organization: {}", organizationId))
                .doOnError(error -> log.error("Error retrieving POIs for organization {}: {}",
                        organizationId, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved all POIs for organization: {}", organizationId))
                .doOnError(error -> log.error("Error retrieving all POIs for organization {}: {}",
                        organizationId, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findByLocationWithinRadius(Double latitude, Double longitude,
            Double radiusKm) {
        return repository.findByLocationWithinRadius(latitude, longitude, radiusKm)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Location search completed"))
                .doOnError(error -> log.error("Error in location search: {}", error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findByType(com.poi.yow_point.application.model.PoiType poiType) {
        return repository.findByPoiType(poiType)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved POIs by type: {}", poiType))
                .doOnError(error -> log.error("Error retrieving POIs by type {}: {}", poiType, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findByCategory(com.poi.yow_point.application.model.PoiCategory poiCategory) {
        return repository.findByPoiCategory(poiCategory)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved POIs by category: {}", poiCategory))
                .doOnError(error -> log.error("Error retrieving POIs by category {}: {}",
                        poiCategory, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> searchByName(String name) {
        return repository.findByPoiNameContainingIgnoreCase(name)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Name search completed for: {}", name))
                .doOnError(error -> log.error("Error in name search for {}: {}", name, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findByCity(String city) {
        return repository.findByAddressCity(city)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved POIs for city: {}", city))
                .doOnError(error -> log.error("Error retrieving POIs for city {}: {}",
                        city, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findTopPopular(Integer limit) {
        return repository.findTopByPopularityScore(limit)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved top {} popular POIs", limit))
                .doOnError(error -> log.error("Error retrieving top popular POIs: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> deactivatePoi(UUID poiId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable (DELETE) for POI {}: {}", poiId, e.getMessage());
                    return Mono.just(true);
                })
                .then(repository.deactivateById(poiId))
                .defaultIfEmpty((long) 0)
                .flatMap(count -> {
                    if (count > 0) {
                        log.info("POI {} deactivated successfully. Publishing WebSocket event...", poiId);
                        // Fetch the POI reactively and publish the event
                        return repository.findById(poiId)
                                .map(mapper::toDto)
                                .doOnNext(poiDto -> eventPublisher.publishEvent(
                                        new PoiEvent(PoiEvent.EventType.POI_DESACTIVATED, poiDto)))
                                .then();
                    } else {
                        log.warn("No POI found with ID {} to deactivate", poiId);
                        return Mono.empty();
                    }
                })
                .doOnError(error -> log.error("Error deactivating POI {}: {}", poiId, error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> activatePoi(UUID poiId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable (DELETE) for POI {}: {}", poiId, e.getMessage());
                    return Mono.just(true);
                })
                .then(repository.activateById(poiId))
                .defaultIfEmpty((long) 0)
                .flatMap(count -> {
                    if (count > 0) {
                        log.info("POI {} activated successfully. Publishing WebSocket event...", poiId);
                        // Fetch the POI reactively and publish the event
                        return repository.findById(poiId)
                                .map(mapper::toDto)
                                .doOnNext(poiDto -> eventPublisher.publishEvent(
                                        new PoiEvent(PoiEvent.EventType.POI_ACTIVATED, poiDto)))
                                .then();
                    } else {
                        log.warn("No POI found with ID {} to activate", poiId);
                        return Mono.empty();
                    }
                })
                .doOnError(error -> log.error("Error activating POI {}: {}", poiId, error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> deletePoi(UUID poiId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable (DELETE) for POI {}: {}", poiId, e.getMessage());
                    return Mono.just(true);
                })
                .then(repository.findById(poiId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("POI not found with ID: " + poiId)))
                .flatMap(poi -> repository.deleteById(poiId).thenReturn(poi))
                .doOnSuccess(poi -> {
                    log.info("POI {} deleted successfully. Publishing WebSocket event...", poiId);
                    eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_DELETED, null));
                    kafkaProducerService.sendMessage("poi-deleted", mapper.toDto(poi));
                })
                .doOnError(error -> log.error("Error deleting POI {}: {}", poiId, error.getMessage()))
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> updatePopularityScore(UUID poiId, Float score) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .onErrorResume(e -> {
                    log.warn("Redis unavailable (DELETE) for POI {}: {}", poiId, e.getMessage());
                    return Mono.just(true);
                })
                .then(repository.updatePopularityScore(poiId, score))
                .defaultIfEmpty((long) 0)
                .doOnSuccess(count -> {
                    if (count > 0) {
                        log.info("Popularity score updated for POI {}: {}", poiId, score);
                    } else {
                        log.warn("No POI found with ID {} to update popularity score", poiId);
                    }
                })
                .doOnError(error -> log.error("Error updating popularity score for POI {}: {}",
                        poiId, error.getMessage()))
                .then();
    }

    @Override
    public Mono<Long> countActiveByOrganizationId(UUID organizationId) {
        return repository.countActiveByOrganizationId(organizationId)
                .doOnSuccess(count -> log.debug("Active POI count for organization {}: {}",
                        organizationId, count))
                .doOnError(error -> log.error("Error counting POIs for organization {}: {}",
                        organizationId, error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findByCreatedByUserId(UUID userId) {
        return repository.findByCreatedByUserId(userId)
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved POIs created by user: {}", userId))
                .doOnError(error -> log.error("Error retrieving POIs for user {}: {}",
                        userId, error.getMessage()));
    }

    @Override
    public Mono<Boolean> existsByNameAndOrganization(String name, UUID organizationId, UUID excludeId) {
        UUID excludeIdToUse = excludeId != null ? excludeId : UUID.randomUUID();
        return repository.existsByNameAndOrganizationIdExcludingId(name, organizationId, excludeIdToUse)
                .doOnSuccess(exists -> log.debug("POI name '{}' exists in organization {}: {}",
                        name, organizationId, exists))
                .doOnError(error -> log.error("Error checking POI name existence: {}", error.getMessage()));
    }

    @Override
    public Flux<PointOfInterestDTO> findAll() {
        return repository.findAll()
                .map(mapper::toDto)
                .doOnComplete(() -> log.debug("Retrieved all POIs"))
                .doOnError(error -> log.error("Error retrieving all POIs: {}", error.getMessage()));
    }

    private Mono<PointOfInterestDTO> validateDto(PointOfInterestDTO dto) {
        return Mono.fromCallable(() -> {
            Errors errors = new BeanPropertyBindingResult(dto, "pointOfInterestDTO");
            validator.validate(dto, errors);
            if (errors.hasErrors()) {
                throw new IllegalArgumentException(errors.getAllErrors().toString());
            }
            return dto;
        });
    }
}
