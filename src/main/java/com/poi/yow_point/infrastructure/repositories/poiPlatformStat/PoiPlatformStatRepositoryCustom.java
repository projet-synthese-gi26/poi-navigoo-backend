package com.poi.yow_point.infrastructure.repositories.poiPlatformStat;

import com.poi.yow_point.infrastructure.entities.PoiPlatformStat;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface PoiPlatformStatRepositoryCustom {

    Flux<PoiPlatformStat> findByOrgIdAndDateRange(UUID orgId, LocalDate startDate, LocalDate endDate);

    Flux<PoiPlatformStat> findByPoiIdAndPlatformTypeAndDateRange(UUID poiId, String platformType, LocalDate startDate,
            LocalDate endDate);
}