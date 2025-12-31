package com.poi.yow_point.infrastructure.repositories.poiReview;

import com.poi.yow_point.infrastructure.entities.PoiReview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.relational.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class PoiReviewRepositoryImpl implements PoiReviewRepositoryCustom {

    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;

    @Override
    public Flux<PoiReview> findByPoiId(UUID poiId) {
        return template.select(PoiReview.class)
                .matching(query(Criteria.where("poi_id").is(poiId)))
                .all();
    }

    @Override
    public Flux<PoiReview> findByUserId(UUID userId) {
        return template.select(PoiReview.class)
                .matching(query(Criteria.where("user_id").is(userId)))
                .all();
    }

    @Override
    public Flux<PoiReview> findByOrganizationId(UUID organizationId) {
        return template.select(PoiReview.class)
                .matching(query(Criteria.where("organization_id").is(organizationId)))
                .all();
    }

    @Override
    public Flux<PoiReview> findByPlatformType(String platformType) {
        return template.select(PoiReview.class)
                .matching(query(Criteria.where("platform_type").is(platformType)))
                .all();
    }

    @Override
    public Flux<PoiReview> findByPoiIdOrderByCreatedAtDesc(UUID poiId) {
        return template.select(PoiReview.class)
                .matching(query(Criteria.where("poi_id").is(poiId))
                        .sort(org.springframework.data.domain.Sort.by("created_at").descending()))
                .all();
    }

    @Override
    public Flux<PoiReview> findByUserIdOrderByCreatedAtDesc(UUID userId) {
        return template.select(PoiReview.class)
                .matching(query(Criteria.where("user_id").is(userId))
                        .sort(org.springframework.data.domain.Sort.by("created_at").descending()))
                .all();
    }

    @Override
    public Mono<Double> findAverageRatingByPoiId(UUID poiId) {
        return databaseClient.sql("SELECT AVG(rating) FROM poi_review WHERE poi_id = :poiId")
                .bind("poiId", poiId)
                .map(row -> row.get(0, Double.class))
                .one()
                .defaultIfEmpty(0.0);
    }

    @Override
    public Mono<Long> countByPoiId(UUID poiId) {
        return databaseClient.sql("SELECT COUNT(*) FROM poi_review WHERE poi_id = :poiId")
                .bind("poiId", poiId)
                .map(row -> row.get(0, Long.class))
                .one()
                .defaultIfEmpty(0L);
    }
}