package com.poi.yow_point.infrastructure.repositories.poiPlatformStat;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.PoiPlatformStat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface PoiPlatformStatRepository
        extends R2dbcRepository<PoiPlatformStat, UUID>, PoiPlatformStatRepositoryCustom {

    // Méthodes de recherche réactives personnalisées
    Flux<PoiPlatformStat> findByOrgId(UUID orgId);

    Flux<PoiPlatformStat> findByPoiId(UUID poiId);

    Flux<PoiPlatformStat> findByPlatformType(String platformType);

    Flux<PoiPlatformStat> findByStatDate(LocalDate statDate);

    Flux<PoiPlatformStat> findByOrgIdAndStatDate(UUID orgId, LocalDate statDate);

    Flux<PoiPlatformStat> findByPoiIdAndStatDate(UUID poiId, LocalDate statDate);

    Flux<PoiPlatformStat> findByOrgIdAndPlatformType(UUID orgId, String platformType);

    Flux<PoiPlatformStat> findByStatDateBetween(LocalDate startDate, LocalDate endDate);

    // Suppression par critères
    Mono<Void> deleteByOrgId(UUID orgId);

    Mono<Void> deleteByPoiId(UUID poiId);
}