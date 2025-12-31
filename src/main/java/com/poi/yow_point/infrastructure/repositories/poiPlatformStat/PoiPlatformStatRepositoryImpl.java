package com.poi.yow_point.infrastructure.repositories.poiPlatformStat;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.PoiPlatformStat;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PoiPlatformStatRepositoryImpl implements PoiPlatformStatRepositoryCustom {

    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public Flux<PoiPlatformStat> findByOrgIdAndDateRange(UUID orgId, LocalDate startDate, LocalDate endDate) {
        return entityTemplate.select(PoiPlatformStat.class)
                .matching(Query.query(Criteria.where("org_id").is(orgId)
                        .and("stat_date").between(startDate, endDate)))
                .all();
    }

    @Override
    public Flux<PoiPlatformStat> findByPoiIdAndPlatformTypeAndDateRange(UUID poiId, String platformType,
            LocalDate startDate, LocalDate endDate) {
        return entityTemplate.select(PoiPlatformStat.class)
                .matching(Query.query(Criteria.where("poi_id").is(poiId)
                        .and("platform_type").is(platformType)
                        .and("stat_date").between(startDate, endDate)))
                .all();
    }
}