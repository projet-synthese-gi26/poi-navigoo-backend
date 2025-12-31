package com.poi.yow_point.application.services.poiAccessLog;

import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public interface PoiAccessLogService {

    Mono<PoiAccessLogDTO> createAccessLog(PoiAccessLogDTO dto);

    Mono<PoiAccessLogDTO> getAccessLogById(UUID accessId);

    Flux<PoiAccessLogDTO> getAllAccessLogs();

    Flux<PoiAccessLogDTO> getAccessLogsByPoiId(UUID poiId);

    Flux<PoiAccessLogDTO> getAccessLogsByOrganizationId(UUID organizationId);

    Flux<PoiAccessLogDTO> getAccessLogsByUserId(UUID userId);

    Flux<PoiAccessLogDTO> getAccessLogsByAccessType(String accessType);

    Flux<PoiAccessLogDTO> getAccessLogsByPlatformType(String platformType);

    Flux<PoiAccessLogDTO> getAccessLogsByPoiAndOrganization(UUID poiId, UUID organizationId);

    Flux<PoiAccessLogDTO> getAccessLogsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate);

    Flux<PoiAccessLogDTO> getRecentAccessLogsByPoiId(UUID poiId, OffsetDateTime since);

    Flux<PoiAccessLogDTO> getAccessLogsByPoiIdWithPagination(UUID poiId, int page, int size);

    Mono<Long> countAccessLogsByPoiId(UUID poiId);

    Mono<Long> countAccessLogsByPoiIdAndAccessType(UUID poiId, String accessType);

    Flux<Map<String, Object>> getPlatformStatsForOrganization(UUID organizationId);

    Mono<PoiAccessLogDTO> updateAccessLog(UUID accessId, PoiAccessLogDTO dto);

    Mono<Void> deleteAccessLog(UUID accessId);

    Mono<Long> deleteOldLogs(OffsetDateTime beforeDate);
}