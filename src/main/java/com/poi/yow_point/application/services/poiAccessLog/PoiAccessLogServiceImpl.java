package com.poi.yow_point.application.services.poiAccessLog;

import com.poi.yow_point.application.mappers.MapperUtils;
import com.poi.yow_point.application.mappers.PoiAccessLogMapper;
import com.poi.yow_point.application.validation.PoiAccessLogValidator;
//import com.poi.yow_point.infrastructure.entities.PoiAccessLog;
import com.poi.yow_point.infrastructure.repositories.poiAccessLog.PoiAccessLogRepository;
import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoiAccessLogServiceImpl implements PoiAccessLogService {

    private final PoiAccessLogRepository repository;
    private final PoiAccessLogMapper mapper;
    private final MapperUtils mapperUtils;
    private final PoiAccessLogValidator validator;

    @Override
    @Transactional
    public Mono<PoiAccessLogDTO> createAccessLog(PoiAccessLogDTO dto) {
        log.debug("Création d'un nouveau log d'accès pour POI: {}", dto.getPoiId());
        return validator.validate(dto)
                .then(Mono.fromCallable(() -> mapper.toEntity(dto)))
                .doOnNext(entity -> {
                    if (entity.getAccessId() == null)
                        entity.setAccessId(UUID.randomUUID());
                    if (entity.getAccessDatetime() == null)
                        entity.setAccessDatetime(OffsetDateTime.now());
                })
                .flatMap(repository::save)
                .map(mapper::toDto)
                .doOnSuccess(result -> log.info("Log d'accès créé avec succès: {}", result.getAccessId()))
                .doOnError(error -> log.error("Erreur lors de la création du log d'accès: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<PoiAccessLogDTO> updateAccessLog(UUID accessId, PoiAccessLogDTO dto) {
        log.debug("Mise à jour du log d'accès: {}", accessId);

        return validator.validate(dto)
                .then(repository.findById(accessId))
                .switchIfEmpty(Mono.error(new RuntimeException("Log d'accès non trouvé: " + accessId)))
                .map(existingEntity -> {
                    if (dto.getPlatformType() != null)
                        existingEntity.setPlatformType(dto.getPlatformType());
                    if (dto.getAccessType() != null)
                        existingEntity.setAccessType(dto.getAccessType());
                    if (dto.getMetadata() != null)
                        existingEntity.setMetadata(mapperUtils.mapToJsonNode(dto.getMetadata()));
                    return existingEntity;
                })
                .flatMap(repository::save)
                .map(mapper::toDto)
                .doOnSuccess(result -> log.info("Log d'accès mis à jour: {}", accessId))
                .doOnError(error -> log.error("Erreur lors de la mise à jour du log {}: {}", accessId,
                        error.getMessage()));
    }

    @Override
    public Mono<PoiAccessLogDTO> getAccessLogById(UUID accessId) {
        log.debug("Recherche du log d'accès: {}", accessId);
        return repository.findById(accessId)
                .map(mapper::toDto)
                .doOnNext(result -> log.debug("Log d'accès trouvé: {}", accessId))
                .switchIfEmpty(Mono.error(new RuntimeException("Log d'accès non trouvé: " + accessId)));
    }

    @Override
    public Flux<PoiAccessLogDTO> getAllAccessLogs() {
        log.debug("Récupération de tous les logs d'accès");
        return repository.findAll().map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByPoiId(UUID poiId) {
        log.debug("Recherche des logs d'accès pour POI: {}", poiId);
        return repository.findByPoiId(poiId).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByOrganizationId(UUID organizationId) {
        return repository.findByOrganizationId(organizationId).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByUserId(UUID userId) {
        return repository.findByUserId(userId).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByAccessType(String accessType) {
        log.debug("Recherche des logs d'accès pour type: {}", accessType);
        return repository.findByAccessType(accessType).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByPlatformType(String platformType) {
        log.debug("Recherche des logs d'accès pour plateforme: {}", platformType);
        return repository.findByPlatformType(platformType).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByPoiAndOrganization(UUID poiId, UUID organizationId) {
        log.debug("Recherche des logs d'accès pour POI: {} et organisation: {}", poiId, organizationId);
        return repository.findByPoiIdAndOrganizationId(poiId, organizationId).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Recherche des logs d'accès entre {} et {}", startDate, endDate);
        return repository.findByAccessDatetimeBetween(startDate, endDate).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getRecentAccessLogsByPoiId(UUID poiId, OffsetDateTime since) {
        log.debug("Recherche des logs d'accès récents pour POI: {} depuis {}", poiId, since);
        return repository.findRecentByPoiId(poiId, since).map(mapper::toDto);
    }

    @Override
    public Flux<PoiAccessLogDTO> getAccessLogsByPoiIdWithPagination(UUID poiId, int page, int size) {
        int offset = page * size;
        log.debug("Recherche paginée des logs d'accès pour POI: {} (page: {}, taille: {})", poiId, page, size);
        return repository.findByPoiIdWithPagination(poiId, size, offset).map(mapper::toDto);
    }

    @Override
    public Mono<Long> countAccessLogsByPoiId(UUID poiId) {
        log.debug("Comptage des accès pour POI: {}", poiId);
        return repository.countByPoiId(poiId);
    }

    @Override
    public Mono<Long> countAccessLogsByPoiIdAndAccessType(UUID poiId, String accessType) {
        log.debug("Comptage des accès de type {} pour POI: {}", accessType, poiId);
        return repository.countByPoiIdAndAccessType(poiId, accessType);
    }

    @Override
    public Flux<Map<String, Object>> getPlatformStatsForOrganization(UUID organizationId) {
        log.debug("Récupération des statistiques par plateforme pour organisation: {}", organizationId);
        return repository.getPlatformStatsForOrganization(organizationId);
    }

    @Override
    @Transactional
    public Mono<Void> deleteAccessLog(UUID accessId) {
        log.debug("Suppression du log d'accès: {}", accessId);
        return repository.findById(accessId)
                .switchIfEmpty(Mono.error(new RuntimeException("Log d'accès non trouvé: " + accessId)))
                .flatMap(repository::delete)
                .doOnSuccess(result -> log.info("Log d'accès supprimé: {}", accessId))
                .doOnError(error -> log.error("Erreur lors de la suppression du log {}: {}", accessId,
                        error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Long> deleteOldLogs(OffsetDateTime beforeDate) {
        log.info("Suppression des logs d'accès antérieurs à: {}", beforeDate);
        return repository.deleteOldLogs(beforeDate)
                .doOnSuccess(count -> log.info("Nombre de logs supprimés: {}", count))
                .doOnError(
                        error -> log.error("Erreur lors de la suppression des anciens logs: {}", error.getMessage()));
    }
}