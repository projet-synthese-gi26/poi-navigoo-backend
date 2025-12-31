package com.poi.yow_point.infrastructure.repositories.poiAccessLog;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.poi.yow_point.infrastructure.entities.PoiAccessLog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PoiAccessLogRepositoryCustom {

    Flux<PoiAccessLog> findByPoiId(UUID poiId);

    Flux<PoiAccessLog> findByOrganizationId(UUID organizationId);

    Flux<PoiAccessLog> findByUserId(UUID userId);

    Flux<PoiAccessLog> findByAccessType(String accessType);

    Flux<PoiAccessLog> findByPlatformType(String platformType);

    Flux<PoiAccessLog> findByPoiIdAndOrganizationId(UUID poiId, UUID organizationId);

    Flux<PoiAccessLog> findByAccessDatetimeBetween(OffsetDateTime startDate, OffsetDateTime endDate);

    Flux<PoiAccessLog> findRecentByPoiId(UUID poiId, OffsetDateTime since);

    Mono<Long> countByPoiId(UUID poiId);

    Mono<Long> countByPoiIdAndAccessType(UUID poiId, String accessType);

    Mono<Long> deleteOldLogs(OffsetDateTime beforeDate);

    Flux<PoiAccessLog> findByPoiIdWithPagination(UUID poiId, int limit, int offset);

    Flux<Map<String, Object>> getPlatformStatsForOrganization(UUID organizationId);
}
