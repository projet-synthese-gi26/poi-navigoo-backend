package com.poi.yow_point.application.services.point_of_interest;

import com.poi.yow_point.application.mappers.MapperUtils;
import com.poi.yow_point.application.mappers.PointOfInterestMapper;
import com.poi.yow_point.application.model.PoiStatus;
import com.poi.yow_point.application.services.appUser.AppUserService;
import com.poi.yow_point.application.services.notification.NotificationService;
import com.poi.yow_point.application.services.websocket.PoiEventPublisher;
import com.poi.yow_point.application.validation.PointOfInterestValidator;
import com.poi.yow_point.infrastructure.kafka.KafkaProducerService;
import com.poi.yow_point.presentation.dto.CreatePoiDTO;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.UpdatePoiDTO;
import com.poi.yow_point.presentation.dto.websocketDTO.PoiEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Mono<PointOfInterestDTO> createPoi(CreatePoiDTO dto) {
        return Mono.just(dto) // Skipping complex validation for now as logic changed, relying on partial checks
                .flatMap(validDto -> repository.existsByNameAndOrganizationIdExcludingId(
                        validDto.getPoiName(),
                        validDto.getOrganizationId(),
                        UUID.randomUUID())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException(
                                        "A POI with this name already exists in the organization"));
                            }
                            return Mono.just(validDto);
                        }))
                .map(validDto -> mapper.toEntity(validDto, mapperUtils))
                .doOnNext(entity -> {
                    Instant now = Instant.now();
                    entity.setCreatedAt(now);
                    entity.setUpdatedAt(now);
                    entity.setIsActive(false); // Default false
                    entity.setStatus(PoiStatus.SUBMITTED); // Default Submitted
                    entity.setPopularityScore(0.0f);
                })
                .flatMap(repository::save)
                .map(mapper::toDto)
                .doOnSuccess(savedDto -> {
                    log.info("POI created successfully with ID: {}. Status: SUBMITTED.", savedDto.getPoiId());
                    
                    // Send Kafka message
                    kafkaProducerService.sendMessage("poi-created", savedDto);

                    // Notify user about submission
                    if (savedDto.getCreatedByUserId() != null) {
                        appUserService.getUserById(savedDto.getCreatedByUserId())
                                .flatMap(userDto -> notificationService.notifyPoiSubmitted(savedDto, userDto))
                                .onErrorResume(e -> {
                                    log.error("Failed to send submission notification: {}", e.getMessage());
                                    return Mono.empty();
                                })
                                .subscribe();
                    }
                })
                .doOnError(error -> log.error("Error creating POI: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<PointOfInterestDTO> updatePoi(UUID poiId, UpdatePoiDTO dto) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .then(repository.findById(poiId))
                .switchIfEmpty(Mono.error(new RuntimeException("POI not found with ID: " + poiId)))
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
                    existingEntity.setUpdatedAt(Instant.now());
                    return existingEntity;
                })
                .flatMap(repository::save)
                .map(mapper::toDto)
                .doOnSuccess(updatedDto -> {
                    log.info("POI updated successfully: {}", updatedDto.getPoiId());
                    eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_UPDATED, updatedDto));
                    kafkaProducerService.sendMessage("poi-updated", updatedDto);
                })
                .doOnError(error -> log.error("Error updating POI {}: {}", poiId, error.getMessage()));
    }

    @Override
    public Mono<PointOfInterestDTO> findById(UUID poiId) {
        String cacheKey = CACHE_KEY_PREFIX + poiId;
        return redisTemplate.opsForValue().get(cacheKey)
                .onErrorResume(e -> Mono.empty())
                .switchIfEmpty(repository.findById(poiId)
                        .map(mapper::toDto)
                        .flatMap(dto -> redisTemplate.opsForValue().set(cacheKey, dto, CACHE_TTL).thenReturn(dto)));
    }

    @Override
    public Flux<PointOfInterestDTO> findActiveByOrganizationId(UUID organizationId) {
        return repository.findActiveByOrganizationId(organizationId)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findByLocationWithinRadius(Double latitude, Double longitude, Double radiusKm) {
        return repository.findByLocationWithinRadius(latitude, longitude, radiusKm)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findByType(com.poi.yow_point.application.model.PoiType poiType) {
        return repository.findByPoiType(poiType)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findByCategory(com.poi.yow_point.application.model.PoiCategory poiCategory) {
        return repository.findByPoiCategory(poiCategory)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> searchByName(String name) {
        return repository.findByPoiNameContainingIgnoreCase(name)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findByCity(String city) {
        return repository.findByAddressCity(city)
                .map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findTopPopular(Integer limit) {
        return repository.findTopByPopularityScore(limit)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public Mono<Void> deactivatePoi(UUID poiId, String reason, UUID deactivatedByUserId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .then(repository.findById(poiId))
                .flatMap(poi -> {
                    poi.setIsActive(false);
                    poi.setDeactivationReason(reason);
                    poi.setDeactivatedByUserId(deactivatedByUserId);
                    poi.setUpdatedAt(Instant.now());
                    return repository.save(poi);
                })
                .doOnSuccess(saved -> {
                     log.info("POI {} deactivated by user {}", poiId, deactivatedByUserId);
                     eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_DESACTIVATED, mapper.toDto(saved)));
                })
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> activatePoi(UUID poiId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .then(repository.findById(poiId))
                .flatMap(poi -> {
                    poi.setIsActive(true);
                    poi.setDeactivationReason(null);
                    poi.setDeactivatedByUserId(null);
                    poi.setUpdatedAt(Instant.now());
                    return repository.save(poi);
                })
                .doOnSuccess(saved -> {
                    log.info("POI {} activated", poiId);
                    eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_ACTIVATED, mapper.toDto(saved)));
                })
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> deletePoi(UUID poiId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .then(repository.findById(poiId))
                .flatMap(poi -> repository.deleteById(poiId).then(Mono.just(poi)))
                .doOnSuccess(poi -> {
                    log.info("POI {} deleted", poiId);
                    eventPublisher.publishEvent(new PoiEvent(PoiEvent.EventType.POI_DELETED, null));
                    kafkaProducerService.sendMessage("poi-deleted", mapper.toDto(poi));
                })
                .then();
    }

    @Override
    public Mono<Long> countActiveByOrganizationId(UUID organizationId) {
        return repository.countActiveByOrganizationId(organizationId);
    }

    @Override
    public Flux<PointOfInterestDTO> findByCreatedByUserId(UUID userId) {
        return repository.findByCreatedByUserId(userId).map(mapper::toDto);
    }

    @Override
    public Mono<Boolean> existsByNameAndOrganization(String name, UUID organizationId, UUID excludeId) {
        UUID excludeIdToUse = excludeId != null ? excludeId : UUID.randomUUID();
        return repository.existsByNameAndOrganizationIdExcludingId(name, organizationId, excludeIdToUse);
    }

    @Override
    public Flux<PointOfInterestDTO> findAll() {
        return repository.findAll().map(mapper::toDto);
    }

    @Override
    public Mono<Long> countAll() {
        return repository.count();
    }

    @Override
    public Flux<PointOfInterestDTO> findRecent(Integer limit) {
        return repository.findRecent(limit).map(mapper::toDto);
    }

    // --- New Methods ---

    @Override
    public Flux<PointOfInterestDTO> findSubmittedPois() {
        return repository.findByStatus(PoiStatus.SUBMITTED).map(mapper::toDto);
    }

    @Override
    public Flux<PointOfInterestDTO> findApprovedPois() {
        return repository.findByStatus(PoiStatus.APPROUVED).map(mapper::toDto);
    }

    @Override
    @Transactional
    public Mono<Void> approvePoi(UUID poiId, UUID approverId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .then(repository.findById(poiId))
                .flatMap(poi -> {
                    poi.setStatus(PoiStatus.APPROUVED);
                    poi.setApprouvedByUserId(approverId);
                    poi.setUpdatedAt(Instant.now());
                    return repository.save(poi);
                })
                .doOnSuccess(saved -> {
                    // Notify user about approval
                    if (saved.getCreatedByUserId() != null) {
                        appUserService.getUserById(saved.getCreatedByUserId())
                                .flatMap(userDto -> notificationService.notifyPoiApproved(mapper.toDto(saved), userDto))
                                .onErrorResume(e -> {
                                    log.error("Failed to send approval notification: {}", e.getMessage());
                                    return Mono.empty();
                                })
                                .subscribe();
                    }
                })
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> rejectPoi(UUID poiId, UUID rejecterId) {
        return redisTemplate.opsForValue().delete(CACHE_KEY_PREFIX + poiId)
                .then(repository.findById(poiId))
                .flatMap(poi -> {
                    // Notify user about rejection BEFORE deleting (so we have user data)
                    if (poi.getCreatedByUserId() != null) {
                        appUserService.getUserById(poi.getCreatedByUserId())
                                .flatMap(userDto -> notificationService.notifyPoiRejected(mapper.toDto(poi), userDto))
                                .onErrorResume(e -> {
                                    log.error("Failed to send rejection notification: {}", e.getMessage());
                                    return Mono.empty();
                                })
                                .subscribe();
                    }
                    return repository.delete(poi);
                })
                .then();
    }
}
