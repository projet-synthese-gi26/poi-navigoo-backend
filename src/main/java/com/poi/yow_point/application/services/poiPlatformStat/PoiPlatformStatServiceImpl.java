package com.poi.yow_point.application.services.poiPlatformStat;

import com.poi.yow_point.application.mappers.PoiPlatformStatMapper;
import com.poi.yow_point.application.validation.PoiPlatformStatValidator;
import com.poi.yow_point.infrastructure.entities.PoiPlatformStat;
import com.poi.yow_point.infrastructure.repositories.poiPlatformStat.PoiPlatformStatRepository;
import com.poi.yow_point.presentation.dto.PoiPlatformStatDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PoiPlatformStatServiceImpl implements PoiPlatformStatService {

    private final PoiPlatformStatRepository repository;
    private final PoiPlatformStatMapper mapper;
    private final R2dbcEntityTemplate entityTemplate;
    private final PoiPlatformStatValidator validator;

    @Override
    public Mono<PoiPlatformStatDTO> createStat(PoiPlatformStatDTO statDTO) {
        Errors errors = new BeanPropertyBindingResult(statDTO, "poiPlatformStatDTO");
        validator.validate(statDTO, errors);

        if (errors.hasErrors()) {
            return Mono.error(new IllegalArgumentException("Validation failed: " + errors.getAllErrors()));
        }

        return Mono.just(statDTO)
                .map(dto -> {
                    PoiPlatformStat entity = mapper.toEntity(dto);
                    if (entity.getStatId() == null) {
                        entity.setStatId(UUID.randomUUID());
                    }
                    if (entity.getStatDate() == null) {
                        entity.setStatDate(LocalDate.now());
                    }
                    return entity;
                })
                .flatMap(entity -> entityTemplate.insert(PoiPlatformStat.class)
                        .using(entity)
                        .map(mapper::toDTO))
                .doOnSuccess(dto -> log.info("Statistic created with ID: {}", dto.getStatId()))
                .doOnError(error -> log.error("Error creating statistic", error));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getAllStats() {
        return repository.findAll()
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved all statistics"));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PoiPlatformStatDTO> getStatById(UUID statId) {
        return repository.findById(statId)
                .map(mapper::toDTO)
                .doOnSuccess(dto -> log.info("Found statistic: {}", statId))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Statistic not found with ID: {}", statId);
                    return Mono.empty();
                }));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getStatsByOrgId(UUID orgId) {
        return repository.findByOrgId(orgId)
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved statistics for organization: {}", orgId));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getStatsByPoiId(UUID poiId) {
        return repository.findByPoiId(poiId)
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved statistics for POI: {}", poiId));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getStatsByPlatformType(String platformType) {
        return repository.findByPlatformType(platformType)
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved statistics for platform: {}", platformType));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getStatsByDate(LocalDate statDate) {
        return repository.findByStatDate(statDate)
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved statistics for date: {}", statDate));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getStatsByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByStatDateBetween(startDate, endDate)
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved statistics between {} and {}", startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PoiPlatformStatDTO> getStatsByOrgIdAndDateRange(UUID orgId, LocalDate startDate, LocalDate endDate) {
        return repository.findByOrgIdAndDateRange(orgId, startDate, endDate)
                .map(mapper::toDTO)
                .doOnComplete(() -> log.info("Retrieved statistics for organization {} between {} and {}", orgId,
                        startDate, endDate));
    }

    @Override
    public Mono<PoiPlatformStatDTO> updateStat(UUID statId, PoiPlatformStatDTO statDTO) {
        Errors errors = new BeanPropertyBindingResult(statDTO, "poiPlatformStatDTO");
        validator.validate(statDTO, errors);

        if (errors.hasErrors()) {
            return Mono.error(new IllegalArgumentException("Validation failed: " + errors.getAllErrors()));
        }

        return repository.findById(statId)
                .switchIfEmpty(Mono.error(new RuntimeException("Statistic not found with ID: " + statId)))
                .map(existingStat -> {
                    PoiPlatformStat updatedStat = mapper.toEntity(statDTO);
                    updatedStat.setStatId(statId); // Keep original ID
                    return updatedStat;
                })
                .flatMap(repository::save)
                .map(mapper::toDTO)
                .doOnSuccess(dto -> log.info("Statistic updated: {}", statId))
                .doOnError(error -> log.error("Error updating statistic: {}", statId, error));
    }

    @Override
    public Mono<Void> deleteStat(UUID statId) {
        return repository.findById(statId)
                .switchIfEmpty(Mono.error(new RuntimeException("Statistic not found with ID: " + statId)))
                .flatMap(stat -> repository.deleteById(statId))
                .doOnSuccess(v -> log.info("Statistic deleted: {}", statId))
                .doOnError(error -> log.error("Error deleting statistic: {}", statId, error));
    }

    @Override
    public Mono<Void> deleteStatsByOrgId(UUID orgId) {
        return repository.deleteByOrgId(orgId)
                .doOnSuccess(v -> log.info("Statistics deleted for organization: {}", orgId))
                .doOnError(error -> log.error("Error deleting statistics for organization: {}", orgId, error));
    }

    @Override
    public Mono<Void> deleteStatsByPoiId(UUID poiId) {
        return repository.deleteByPoiId(poiId)
                .doOnSuccess(v -> log.info("Statistics deleted for POI: {}", poiId))
                .doOnError(error -> log.error("Error deleting statistics for POI: {}", poiId, error));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existsById(UUID statId) {
        return repository.existsById(statId)
                .doOnNext(exists -> log.debug("Statistic {} exists: {}", statId, exists));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAll() {
        return repository.count()
                .doOnNext(count -> log.debug("Total statistics count: {}", count));
    }
}
