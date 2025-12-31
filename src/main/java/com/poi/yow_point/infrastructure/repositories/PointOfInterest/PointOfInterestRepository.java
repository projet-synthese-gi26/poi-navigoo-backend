package com.poi.yow_point.infrastructure.repositories.PointOfInterest;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.PointOfInterest;

import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PointOfInterestRepository
                extends R2dbcRepository<PointOfInterest, UUID>, PointOfInterestRepositoryCustom {

        Flux<PointOfInterest> findActiveByOrganizationId(UUID organizationId);

        Flux<PointOfInterest> findByOrganizationId(UUID organizationId);

        Flux<PointOfInterest> findByPoiType(com.poi.yow_point.application.model.PoiType poiType);

        Flux<PointOfInterest> findByPoiCategory(com.poi.yow_point.application.model.PoiCategory poiCategory);

        Flux<PointOfInterest> findByPoiNameContainingIgnoreCase(String poiName);

        Flux<PointOfInterest> findByAddressCity(String addressCity);

        Flux<PointOfInterest> findByCreatedByUserId(UUID userId);

}