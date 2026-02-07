package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.point_of_interest.PointOfInterestService;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.CreatePoiDTO;
import com.poi.yow_point.presentation.dto.UpdatePoiDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/pois")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Points of Interest", description = "API for managing Points of Interest (POI)")
public class PointOfInterestController {

        private final PointOfInterestService poiService;

        @GetMapping
        @Operation(summary = "Get all POIs", description = "Retrieves all Points of Interest")
        public Flux<PointOfInterestDTO> getAllPois() {
                log.info("REST request to get all POIs");
                return poiService.findAll()
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving all POIs", ex);
                                                        return Flux.empty();
                                                });
        }

        @PostMapping
        @Operation(summary = "Create a new POI", description = "Creates a new Point of Interest with the provided information")
        public Mono<ResponseEntity<PointOfInterestDTO>> createPoi(
                        @Parameter(description = "Data for the POI to create", required = true) @RequestBody CreatePoiDTO dto) {
                log.info("REST request to create POI: {}", dto.getPoiName());

                return poiService.createPoi(dto)
                                .map(savedDto -> ResponseEntity.status(HttpStatus.CREATED).body(savedDto))
                                .onErrorResume(IllegalArgumentException.class,
                                                ex -> Mono.just(ResponseEntity.badRequest().build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error creating POI", ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @PutMapping("/{poi_id}")
        @Operation(summary = "Update an existing POI", description = "Updates an existing Point of Interest within new information")
        public Mono<ResponseEntity<PointOfInterestDTO>> updatePoi(
                        @Parameter(description = "ID of the POI to update", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "New data for the POI", required = true) @RequestBody UpdatePoiDTO dto) {
                log.info("REST request to update POI: {}", poiId);

                return poiService.updatePoi(poiId, dto)
                                .map(updatedDto -> ResponseEntity.ok(updatedDto))
                                .onErrorResume(IllegalArgumentException.class,
                                                ex -> Mono.just(ResponseEntity.badRequest().build()))
                                .onErrorResume(RuntimeException.class,
                                                ex -> Mono.just(ResponseEntity.notFound().build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error updating POI: {}", poiId, ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @GetMapping("/{poi_id}")
        @Operation(summary = "Get a POI by ID", description = "Retrieves the details of a specific Point of Interest")
        public Mono<ResponseEntity<PointOfInterestDTO>> getPoiById(
                        @Parameter(description = "ID of the POI", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
                log.debug("REST request to get POI: {}", poiId);

                return poiService.findById(poiId)
                                .map(dto -> ResponseEntity.ok(dto))
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving POI: {}", poiId, ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @PatchMapping("/{poi_id}/desactivate")
        @Operation(summary = "Deactivate a POI", description = "Deactivates a Point of Interest with a reason")
        public Mono<ResponseEntity<Void>> deactivatePoi(
                        @PathVariable("poi_id") UUID poiId,
                        @RequestParam String deactivationReason,
                        @RequestParam UUID deactivatedByUserId) {
                log.info("REST request to deactivate POI: {} by user {}", poiId, deactivatedByUserId);

                return poiService.deactivatePoi(poiId, deactivationReason, deactivatedByUserId)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error deactivating POI: {}", poiId, ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @PatchMapping("/{poi_id}/activate")
        @Operation(summary = "Reactivate a POI", description = "Reactivates a previously deactivated Point of Interest")
        public Mono<ResponseEntity<Void>> activatePoi(
                        @PathVariable("poi_id") UUID poiId) {
                log.info("REST request to activate POI: {}", poiId);

                return poiService.activatePoi(poiId)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error activating POI: {}", poiId, ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @DeleteMapping("/{poi_id}")
        @Operation(summary = "Permanently delete a POI", description = "Permanently deletes a Point of Interest from the database")
        public Mono<ResponseEntity<Void>> deletePoi(
                        @PathVariable("poi_id") UUID poiId) {
                log.info("REST request to delete POI: {}", poiId);

                return poiService.deletePoi(poiId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorResume(RuntimeException.class,
                                                ex -> Mono.just(ResponseEntity.notFound().build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error deleting POI: {}", poiId, ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        // Removed popularity endpoint as requested

        @GetMapping("/submitted")
        @Operation(summary = "Get Submitted POIs", description = "Retrieves all Points of Interest with status SUBMITTED")
        public Flux<PointOfInterestDTO> getSubmittedPois() {
                log.debug("REST request to get submitted POIs");
                return poiService.findSubmittedPois()
                        .onErrorResume(Exception.class,
                                ex -> {
                                        log.error("Error retrieving submitted POIs", ex);
                                        return Flux.empty();
                                });
        }

        @GetMapping("/approved")
        @Operation(summary = "Get Approved POIs", description = "Retrieves all Points of Interest with status APPROUVED")
        public Flux<PointOfInterestDTO> getApprovedPois() {
                log.debug("REST request to get approved POIs");
                return poiService.findApprovedPois()
                        .onErrorResume(Exception.class,
                                ex -> {
                                        log.error("Error retrieving approved POIs", ex);
                                        return Flux.empty();
                                });
        }

        @PatchMapping("/{poi_id}/approve")
        @Operation(summary = "Approve a POI", description = "Approves a Submitted Point of Interest")
        public Mono<ResponseEntity<Void>> approvePoi(
                @PathVariable("poi_id") UUID poiId,
                @RequestParam UUID approverId) {
                log.info("REST request to approve POI: {} by user {}", poiId, approverId);
                return poiService.approvePoi(poiId, approverId)
                        .then(Mono.just(ResponseEntity.ok().<Void>build()))
                        .onErrorResume(Exception.class,
                                ex -> {
                                    log.error("Error approving POI: {}", poiId, ex);
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                });
        }

        @PatchMapping("/{poi_id}/reject")
        @Operation(summary = "Reject a POI", description = "Rejects a Submitted Point of Interest and deletes it")
        public Mono<ResponseEntity<Void>> rejectPoi(
                @PathVariable("poi_id") UUID poiId,
                @RequestParam UUID rejecterId) {
                log.info("REST request to reject POI: {} by user {}", poiId, rejecterId);
                return poiService.rejectPoi(poiId, rejecterId)
                        .then(Mono.just(ResponseEntity.ok().<Void>build()))
                         .onErrorResume(Exception.class,
                                ex -> {
                                    log.error("Error rejecting POI: {}", poiId, ex);
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                });
        }

        // Keeping other methods...

        @GetMapping("/organization/{organization_id}")
        public Flux<PointOfInterestDTO> getPoisByOrganization(@PathVariable("organization_id") UUID organizationId) {
                return poiService.findActiveByOrganizationId(organizationId);
        }

        @GetMapping("/organization/{organization_id}/all")
        public Flux<PointOfInterestDTO> getAllPoisByOrganization(@PathVariable("organization_id") UUID organizationId) {
                return poiService.findByOrganizationId(organizationId);
        }

        @GetMapping("/nearby")
        public Flux<PointOfInterestDTO> getPoisByLocation(
                        @RequestParam Double latitude,
                        @RequestParam Double longitude,
                        @RequestParam(defaultValue = "10.0") Double radiusKm) {
                return poiService.findByLocationWithinRadius(latitude, longitude, radiusKm);
        }

        @GetMapping("/type/{type}")
        public Flux<PointOfInterestDTO> getPoisByType(@PathVariable com.poi.yow_point.application.model.PoiType type) {
                return poiService.findByType(type);
        }

        @GetMapping("/category/{category}")
        public Flux<PointOfInterestDTO> getPoisByCategory(@PathVariable com.poi.yow_point.application.model.PoiCategory category) {
                return poiService.findByCategory(category);
        }

        @GetMapping("/name/{name}")
        public Flux<PointOfInterestDTO> searchPoisByName(@PathVariable String name) {
                return poiService.searchByName(name);
        }

        @GetMapping("/city/{city}")
        public Flux<PointOfInterestDTO> getPoisByCity(@PathVariable String city) {
                return poiService.findByCity(city);
        }

        @GetMapping("/popular")
        public Flux<PointOfInterestDTO> getTopPopularPois(@RequestParam(defaultValue = "10") Integer limit) {
                return poiService.findTopPopular(limit);
        }

        @GetMapping("/user/{user_id}")
        public Flux<PointOfInterestDTO> getPoisByUser(@PathVariable("user_id") UUID userId) {
                return poiService.findByCreatedByUserId(userId);
        }

        @GetMapping("/organization/{organization_id}/count")
        public Mono<ResponseEntity<Long>> countActivePoisByOrganization(@PathVariable("organization_id") UUID organizationId) {
                 return poiService.countActiveByOrganizationId(organizationId)
                        .map(ResponseEntity::ok);
        }

        @GetMapping("/check-name")
        public Mono<ResponseEntity<Boolean>> checkPoiNameExists(
                        @RequestParam String name,
                        @RequestParam UUID organizationId,
                        @RequestParam(required = false) UUID excludeId) {
                return poiService.existsByNameAndOrganization(name, organizationId, excludeId)
                        .map(ResponseEntity::ok);
        }

        @GetMapping("/count")
        public Mono<ResponseEntity<Long>> getPoiCount() {
                return poiService.countAll().map(ResponseEntity::ok);
        }

        @GetMapping("/recent")
        public Flux<PointOfInterestDTO> getRecentPois(@RequestParam(defaultValue = "10") Integer limit) {
                return poiService.findRecent(limit);
        }
}