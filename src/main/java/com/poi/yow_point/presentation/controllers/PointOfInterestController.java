package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.point_of_interest.PointOfInterestService;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;

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

//import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/pois")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Points of Interest", description = "API for managing Points of Interest (POI)")
public class PointOfInterestController {

        private final PointOfInterestService poiService;

        @PostMapping
        @Operation(summary = "Create a new POI", description = "Creates a new Point of Interest with the provided information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "POI created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<PointOfInterestDTO>> createPoi(
                        @Parameter(description = "Data for the POI to create", required = true) @RequestBody PointOfInterestDTO dto) {
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
        @Operation(summary = "Update an existing POI", description = "Updates an existing Point of Interest with new information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "POI updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                        @ApiResponse(responseCode = "404", description = "POI not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<PointOfInterestDTO>> updatePoi(
                        @Parameter(description = "ID of the POI to update", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "New data for the POI", required = true) @RequestBody PointOfInterestDTO dto) {
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
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "POI found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "404", description = "POI not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
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

        @GetMapping("/organization/{organization_id}")
        @Operation(summary = "Get active POIs by organization", description = "Retrieves all active Points of Interest belonging to an organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of active POIs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getPoisByOrganization(
                        @Parameter(description = "ID of the organization", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organization_id") UUID organizationId) {
                log.debug("REST request to get POIs for organization: {}", organizationId);

                return poiService.findActiveByOrganizationId(organizationId)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving POIs for organization: {}",
                                                                        organizationId, ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/organization/{organization_id}/all")
        @Operation(summary = "Get all POIs by organization", description = "Retrieves all Points of Interest (both active and inactive) belonging to an organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of all POIs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getAllPoisByOrganization(
                        @Parameter(description = "ID of the organization", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organization_id") UUID organizationId) {
                log.debug("REST request to get all POIs for organization: {}", organizationId);

                return poiService.findByOrganizationId(organizationId)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving all POIs for organization: {}",
                                                                        organizationId, ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/location")
        @Operation(summary = "Search for POIs by location", description = "Searches for Points of Interest within a given radius around a geographical position")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "POIs found in the area", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid location parameters", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getPoisByLocation(
                        @Parameter(description = "Latitude", required = true, example = "3.8480") @RequestParam Double latitude,
                        @Parameter(description = "Longitude", required = true, example = "11.5021") @RequestParam Double longitude,
                        @Parameter(description = "Search radius in kilometers", example = "5.0") @RequestParam(defaultValue = "10.0") Double radiusKm) {
                log.debug("REST request to get POIs by location: {}, {} within {} km",
                                latitude, longitude, radiusKm);

                return poiService.findByLocationWithinRadius(latitude, longitude, radiusKm)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error in location-based POI search", ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/type/{type}")
        @Operation(summary = "Get POIs by type", description = "Retrieves all Points of Interest of a specific type")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of POIs of the specified type", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getPoisByType(
                        @Parameter(description = "Type of the POI", required = true, example = "RESTAURANT") @PathVariable com.poi.yow_point.application.model.PoiType type) {
                log.debug("REST request to get POIs by type: {}", type);

                return poiService.findByType(type)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving POIs by type: {}", type, ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/category/{category}")
        @Operation(summary = "Get POIs by category", description = "Retrieves all Points of Interest of a specific category")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of POIs of the specified category", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getPoisByCategory(
                        @Parameter(description = "Category of the POI", required = true, example = "FOOD_DRINK") @PathVariable com.poi.yow_point.application.model.PoiCategory category) {
                log.debug("REST request to get POIs by category: {}", category);

                return poiService.findByCategory(category)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving POIs by category: {}", category,
                                                                        ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/name/{name}")
        @Operation(summary = "Search POIs by name", description = "Searches for Points of Interest by their name (partial match)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "POIs matching the search name", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> searchPoisByName(
                        @Parameter(description = "Name or part of the name to search for", required = true, example = "hotel") @PathVariable String name) {
                log.debug("REST request to search POIs by name: {}", name);

                return poiService.searchByName(name)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error searching POIs by name: {}", name, ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/city/{city}")
        @Operation(summary = "Get POIs by city", description = "Retrieves all Points of Interest located in a specific city")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of POIs in the city", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getPoisByCity(
                        @Parameter(description = "Name of the city", required = true, example = "Yaoundé") @PathVariable String city) {
                log.debug("REST request to get POIs by city: {}", city);

                return poiService.findByCity(city)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving POIs by city: {}", city, ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/popular")
        @Operation(summary = "Get most popular POIs", description = "Retrieves the highest-rated/most popular Points of Interest")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of popular POIs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getTopPopularPois(
                        @Parameter(description = "Maximum number of POIs to return", example = "10") @RequestParam(defaultValue = "10") Integer limit) {
                log.debug("REST request to get top {} popular POIs", limit);

                return poiService.findTopPopular(limit)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving popular POIs", ex);
                                                        return Flux.empty();
                                                });
        }

        @GetMapping("/user/{user_id}")
        @Operation(summary = "Get POIs created by a user", description = "Retrieves all Points of Interest created by a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "List of POIs created by the user", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PointOfInterestDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Flux<PointOfInterestDTO> getPoisByUser(
                        @Parameter(description = "ID of the user", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("user_id") UUID userId) {
                log.debug("REST request to get POIs created by user: {}", userId);

                return poiService.findByCreatedByUserId(userId)
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error retrieving POIs for user: {}", userId, ex);
                                                        return Flux.empty();
                                                });
        }

        @PatchMapping("/{poi_id}/desactivate")
        @Operation(summary = "Deactivate a POI", description = "Deactivates a Point of Interest (soft delete)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "POI deactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "POI not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<Void>> deactivatePoi(
                        @Parameter(description = "ID of the POI to deactivate", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
                log.info("REST request to deactivate POI: {}", poiId);

                return poiService.deactivatePoi(poiId)
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
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "POI reactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "POI not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<Void>> activatePoi(
                        @Parameter(description = "ID of the POI to reactivate", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
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
        @Operation(summary = "Permanently delete a POI", description = "Permanently deletes a Point of Interest from the database (hard delete)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "POI deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "POI not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<Void>> deletePoi(
                        @Parameter(description = "ID of the POI to delete", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId) {
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

        @PatchMapping("/{poi_id}/popularity")
        @Operation(summary = "Update popularity score", description = "Updates the popularity score of a Point of Interest (0-100)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Popularity score updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid score (must be between 0 and 100)", content = @Content),
                        @ApiResponse(responseCode = "404", description = "POI not found", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<Void>> updatePopularityScore(
                        @Parameter(description = "ID of the POI", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "New popularity score (0-100)", required = true, example = "85.5") @RequestParam Float score) {
                log.info("REST request to update popularity score for POI: {} to {}", poiId, score);

                if (score < 0 || score > 100) {
                        return Mono.just(ResponseEntity.badRequest().build());
                }

                return poiService.updatePopularityScore(poiId, score)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error updating popularity score for POI: {}", poiId,
                                                                        ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @GetMapping("/organization/{organization_id}/count")
        @Operation(summary = "Count active POIs by organization", description = "Returns the number of active Points of Interest for an organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Number of active POIs", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<Long>> countActivePoisByOrganization(
                        @Parameter(description = "ID of the organization", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable("organization_id") UUID organizationId) {
                log.debug("REST request to count active POIs for organization: {}", organizationId);

                return poiService.countActiveByOrganizationId(organizationId)
                                .map(count -> ResponseEntity.ok(count))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error counting POIs for organization: {}",
                                                                        organizationId, ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @GetMapping("/check-name")
        @Operation(summary = "Check if a POI name exists", description = "Checks if a POI name already exists within an organization (useful for validation)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Result of the check", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Boolean.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
        })
        public Mono<ResponseEntity<Boolean>> checkPoiNameExists(
                        @Parameter(description = "Name of the POI to check", required = true, example = "Hilton Hotel") @RequestParam String name,
                        @Parameter(description = "ID of the organization", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @RequestParam UUID organizationId,
                        @Parameter(description = "ID of the POI to exclude from the check (for updates)", example = "123e4567-e89b-12d3-a456-426614174000") @RequestParam(required = false) UUID excludeId) {
                log.debug("REST request to check POI name existence: {} in organization: {}", name, organizationId);

                return poiService.existsByNameAndOrganization(name, organizationId, excludeId)
                                .map(exists -> ResponseEntity.ok(exists))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error checking POI name existence", ex);
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }
}