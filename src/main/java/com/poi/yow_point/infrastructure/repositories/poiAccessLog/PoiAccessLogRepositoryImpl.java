package com.poi.yow_point.infrastructure.repositories.poiAccessLog;

import com.poi.yow_point.infrastructure.entities.PoiAccessLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor

public class PoiAccessLogRepositoryImpl implements PoiAccessLogRepositoryCustom {

    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;

    @Override
    public Flux<PoiAccessLog> findByPoiId(UUID poiId) {
        log.debug("Recherche des logs d'accès pour POI: {}", poiId);

        Criteria criteria = Criteria.where("poi_id").is(poiId);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findByOrganizationId(UUID organizationId) {
        log.debug("Recherche des logs d'accès pour organisation: {}", organizationId);

        Criteria criteria = Criteria.where("organization_id").is(organizationId);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findByUserId(UUID userId) {
        log.debug("Recherche des logs d'accès pour utilisateur: {}", userId);

        Criteria criteria = Criteria.where("user_id").is(userId);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findByAccessType(String accessType) {
        log.debug("Recherche des logs d'accès pour type: {}", accessType);

        Criteria criteria = Criteria.where("access_type").is(accessType);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findByPlatformType(String platformType) {
        log.debug("Recherche des logs d'accès pour plateforme: {}", platformType);

        Criteria criteria = Criteria.where("platform_type").is(platformType);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findByPoiIdAndOrganizationId(UUID poiId, UUID organizationId) {
        log.debug("Recherche des logs d'accès pour POI: {} et organisation: {}", poiId, organizationId);

        Criteria criteria = Criteria.where("poi_id").is(poiId)
                .and("organization_id").is(organizationId);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findByAccessDatetimeBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Recherche des logs d'accès entre {} et {}", startDate, endDate);

        Criteria criteria = Criteria.where("access_datetime")
                .greaterThanOrEquals(startDate)
                .and("access_datetime").lessThanOrEquals(endDate);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<PoiAccessLog> findRecentByPoiId(UUID poiId, OffsetDateTime since) {
        log.debug("Recherche des logs d'accès récents pour POI: {} depuis {}", poiId, since);

        Criteria criteria = Criteria.where("poi_id").is(poiId)
                .and("access_datetime").greaterThanOrEquals(since);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"));

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Mono<Long> countByPoiId(UUID poiId) {
        log.debug("Comptage des accès pour POI: {}", poiId);

        return databaseClient.sql("SELECT COUNT(*) FROM poi_access_log WHERE poi_id = :poiId")
                .bind("poiId", poiId)
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> countByPoiIdAndAccessType(UUID poiId, String accessType) {
        log.debug("Comptage des accès de type {} pour POI: {}", accessType, poiId);

        return databaseClient
                .sql("SELECT COUNT(*) FROM poi_access_log WHERE poi_id = :poiId AND access_type = :accessType")
                .bind("poiId", poiId)
                .bind("accessType", accessType)
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> deleteOldLogs(OffsetDateTime beforeDate) {
        log.debug("Suppression des logs d'accès antérieurs à: {}", beforeDate);

        return databaseClient.sql("DELETE FROM poi_access_log WHERE access_datetime < :beforeDate")
                .bind("beforeDate", beforeDate)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<PoiAccessLog> findByPoiIdWithPagination(UUID poiId, int limit, int offset) {
        log.debug("Recherche paginée des logs d'accès pour POI: {} (limit: {}, offset: {})", poiId, limit, offset);

        Criteria criteria = Criteria.where("poi_id").is(poiId);
        Query query = Query.query(criteria)
                .sort(Sort.by(Sort.Direction.DESC, "access_datetime"))
                .limit(limit)
                .offset(offset);

        return template.select(PoiAccessLog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Map<String, Object>> getPlatformStatsForOrganization(UUID organizationId) {
        return databaseClient.sql("""
                    SELECT platform_type, COUNT(*) as count
                    FROM poi_access_log
                    WHERE organization_id = :orgId
                    GROUP BY platform_type
                """)
                .bind("orgId", organizationId)
                .map((row, metadata) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("platformType", row.get("platform_type", String.class));
                    map.put("count", row.get("count", Long.class));
                    return map;
                })
                .all();
    }

    /**
     * Recherche par mots-clés dans les métadonnées (PostgreSQL JSONB)
     */
    public Flux<PoiAccessLog> findByMetadataContains(String key, String value) {
        log.debug("Recherche par métadonnées: {} = {}", key, value);

        return databaseClient.sql("""
                SELECT * FROM poi_access_log
                WHERE metadata ->> :key = :value
                ORDER BY access_datetime DESC
                """)
                .bind("key", key)
                .bind("value", value)
                .map(row -> PoiAccessLog.builder()
                        .accessId(row.get("access_id", UUID.class))
                        .poiId(row.get("poi_id", UUID.class))
                        .organizationId(row.get("organization_id", UUID.class))
                        .platformType(row.get("platform_type", String.class))
                        .userId(row.get("user_id", UUID.class))
                        .accessType(row.get("access_type", String.class))
                        .accessDatetime(row.get("access_datetime", OffsetDateTime.class))
                        .build())
                .all();
    }

    /**
     * Statistiques d'accès par période
     */
    public Flux<Object[]> getAccessStatsByPeriod(UUID poiId, OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Statistiques d'accès par période pour POI: {} entre {} et {}", poiId, startDate, endDate);

        return databaseClient.sql("""
                SELECT
                    DATE_TRUNC('day', access_datetime) as day,
                    access_type,
                    COUNT(*) as count
                FROM poi_access_log
                WHERE poi_id = :poiId
                AND access_datetime BETWEEN :startDate AND :endDate
                GROUP BY DATE_TRUNC('day', access_datetime), access_type
                ORDER BY day DESC, access_type
                """)
                .bind("poiId", poiId)
                .bind("startDate", startDate)
                .bind("endDate", endDate)
                .map(row -> new Object[] {
                        row.get("day", OffsetDateTime.class),
                        row.get("access_type", String.class),
                        row.get("count", Long.class)
                })
                .all();
    }
}
