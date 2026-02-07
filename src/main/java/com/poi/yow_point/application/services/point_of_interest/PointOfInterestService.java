package com.poi.yow_point.application.services.point_of_interest;

import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.CreatePoiDTO;
import com.poi.yow_point.presentation.dto.UpdatePoiDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface PointOfInterestService {

    Mono<PointOfInterestDTO> createPoi(CreatePoiDTO dto);

    Mono<PointOfInterestDTO> updatePoi(UUID poiId, UpdatePoiDTO dto);

    Mono<PointOfInterestDTO> findById(UUID poiId);

    Flux<PointOfInterestDTO> findActiveByOrganizationId(UUID organizationId);

    Flux<PointOfInterestDTO> findByOrganizationId(UUID organizationId);

    Flux<PointOfInterestDTO> findByLocationWithinRadius(Double latitude, Double longitude, Double radiusKm);

    Flux<PointOfInterestDTO> findByType(com.poi.yow_point.application.model.PoiType poiType);

    Flux<PointOfInterestDTO> findByCategory(com.poi.yow_point.application.model.PoiCategory poiCategory);

    Flux<PointOfInterestDTO> searchByName(String name);

    Flux<PointOfInterestDTO> findByCity(String city);

    Flux<PointOfInterestDTO> findTopPopular(Integer limit);

    Mono<Void> deactivatePoi(UUID poiId, String reason, UUID deactivatedByUserId);

    Mono<Void> activatePoi(UUID poiId);

    Mono<Void> deletePoi(UUID poiId);

    Mono<Long> countActiveByOrganizationId(UUID organizationId);

    Flux<PointOfInterestDTO> findByCreatedByUserId(UUID userId);

    Mono<Boolean> existsByNameAndOrganization(String name, UUID organizationId, UUID excludeId);

    Flux<PointOfInterestDTO> findAll();

    Mono<Long> countAll();

    Flux<PointOfInterestDTO> findRecent(Integer limit);

    Flux<PointOfInterestDTO> findSubmittedPois();

    Flux<PointOfInterestDTO> findApprovedPois();

    Mono<Void> approvePoi(UUID poiId, UUID approverId);

    Mono<Void> rejectPoi(UUID poiId, UUID rejecterId);
}