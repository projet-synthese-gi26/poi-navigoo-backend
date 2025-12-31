package com.poi.yow_point.application.services.poiPlatformStat;

import com.poi.yow_point.presentation.dto.PoiPlatformStatDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface PoiPlatformStatService {

    Mono<PoiPlatformStatDTO> createStat(PoiPlatformStatDTO statDTO);

    Flux<PoiPlatformStatDTO> getAllStats();

    Mono<PoiPlatformStatDTO> getStatById(UUID statId);

    Flux<PoiPlatformStatDTO> getStatsByOrgId(UUID orgId);

    Flux<PoiPlatformStatDTO> getStatsByPoiId(UUID poiId);

    Flux<PoiPlatformStatDTO> getStatsByPlatformType(String platformType);

    Flux<PoiPlatformStatDTO> getStatsByDate(LocalDate statDate);

    Flux<PoiPlatformStatDTO> getStatsByDateRange(LocalDate startDate, LocalDate endDate);

    Flux<PoiPlatformStatDTO> getStatsByOrgIdAndDateRange(UUID orgId, LocalDate startDate, LocalDate endDate);

    Mono<PoiPlatformStatDTO> updateStat(UUID statId, PoiPlatformStatDTO statDTO);

    Mono<Void> deleteStat(UUID statId);

    Mono<Void> deleteStatsByOrgId(UUID orgId);

    Mono<Void> deleteStatsByPoiId(UUID poiId);

    Mono<Boolean> existsById(UUID statId);

    Mono<Long> countAll();
}