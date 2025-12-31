package com.poi.yow_point.infrastructure.repositories.PointOfInterest;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.PointOfInterest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PointOfInterestRepositoryImpl implements PointOfInterestRepositoryCustom {

        private final R2dbcEntityTemplate entityTemplate;
        private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        @Override
        public Flux<PointOfInterest> findTopByPopularityScore(Integer limit) {
                return entityTemplate.select(PointOfInterest.class)
                                .matching(Query.empty()
                                                .sort(org.springframework.data.domain.Sort.by("popularity_score")
                                                                .descending())
                                                .limit(limit))
                                .all();
        }

        // Implémentation d'une méthode géospatiale avec Criteria
        @Override
        public Flux<PointOfInterest> findByLocationWithinRadius(Double latitude, Double longitude, Double radiusKm) {
                Point center = geometryFactory.createPoint(new Coordinate(longitude, latitude));
                String wkt = String.format("SRID=4326;POINT(%f %f)", longitude, latitude);

                return entityTemplate.getDatabaseClient()
                                .sql("SELECT * FROM point_of_interest WHERE ST_DWithin(location, ST_GeomFromText(:wkt), :distance)")
                                .bind("wkt", wkt)
                                .bind("distance", radiusKm * 1000) // Conversion km → mètres (PostGIS)
                                .map((row, metadata) -> entityTemplate.getConverter().read(PointOfInterest.class, row,
                                                metadata))
                                .all();
        }

        @Override
        public Mono<Long> countActiveByOrganizationId(UUID organizationId) {
                return entityTemplate.count(Query.query(
                                Criteria.where("organization_id").is(organizationId)
                                                .and("is_active").is(true)),
                                PointOfInterest.class);
        }

        @Override
        public Mono<Long> deactivateById(UUID poiId) {
                return entityTemplate.update(PointOfInterest.class)
                                .matching(Query.query(Criteria.where("poi_id").is(poiId)))
                                .apply(Update.update("is_active", false));
        }

        @Override
        public Mono<Long> activateById(UUID poiId) {
                return entityTemplate.update(PointOfInterest.class)
                                .matching(Query.query(Criteria.where("poi_id").is(poiId)))
                                .apply(Update.update("is_active", true));
        }

        @Override
        public Mono<Long> updatePopularityScore(UUID poiId, Float score) {
                return entityTemplate.update(PointOfInterest.class)
                                .matching(Query.query(Criteria.where("poi_id").is(poiId)))
                                .apply(Update.update("popularity_score", score));
        }

        @Override
        public Mono<Boolean> existsByNameAndOrganizationIdExcludingId(String name, UUID organizationId,
                        UUID excludeId) {
                Criteria criteria = Criteria.where("poi_name").is(name)
                                .and("organization_id").is(organizationId);

                if (excludeId != null) {
                        criteria = criteria.and("poi_id").not(excludeId);
                }

                return entityTemplate.exists(Query.query(criteria), PointOfInterest.class);
        }
}