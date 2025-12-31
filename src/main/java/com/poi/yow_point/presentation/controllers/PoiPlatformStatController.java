package com.poi.yow_point.presentation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.poi.yow_point.application.services.poiPlatformStat.PoiPlatformStatService;
import com.poi.yow_point.presentation.dto.PoiPlatformStatDTO;

//import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/poi-platform-stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "POI Platform Statistics", description = "API for managing Point of Interest platform statistics")
public class PoiPlatformStatController {

        private final PoiPlatformStatService service;

        @Operation(summary = "Create a new statistic", description = "Creates a new platform statistic entry")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Statistic created successfully", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping
        public Mono<ResponseEntity<PoiPlatformStatDTO>> createStat(@RequestBody PoiPlatformStatDTO statDTO) {
                return service.createStat(statDTO)
                                .map(createdStat -> ResponseEntity.status(HttpStatus.CREATED).body(createdStat))
                                .onErrorResume(IllegalArgumentException.class,
                                                e -> Mono.just(ResponseEntity.badRequest().build()))
                                .onErrorResume(e -> {
                                        log.error("Error creating statistic", e);
                                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .build());
                                });
        }

        @Operation(summary = "Update a statistic", description = "Updates an existing statistic")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statistic updated successfully", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Statistic not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PutMapping("/{stat_id}")
        public Mono<ResponseEntity<PoiPlatformStatDTO>> updateStat(
                        @Parameter(description = "ID of the statistic to be updated", required = true) @PathVariable("stat_id") UUID statId,
                        @RequestBody PoiPlatformStatDTO statDTO) {
                return service.updateStat(statId, statDTO)
                                .map(ResponseEntity::ok)
                                .onErrorResume(IllegalArgumentException.class,
                                                e -> Mono.just(ResponseEntity.badRequest().build()))
                                .onErrorResume(e -> {
                                        log.error("Error updating statistic: {}", statId, e);
                                        if (e.getMessage().contains("not found")) {
                                                return Mono.just(ResponseEntity.notFound().build());
                                        }
                                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .build());
                                });
        }

        @Operation(summary = "Get all statistics", description = "Retrieves all platform statistics")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping
        public Flux<PoiPlatformStatDTO> getAllStats() {
                return service.getAllStats()
                                .doOnComplete(() -> log.info("Récupération de toutes les statistiques terminée"));
        }

        @Operation(summary = "Get statistic by ID", description = "Retrieves a specific statistic by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Statistic found", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Statistic not found")
        })
        @GetMapping("/{stat_id}")
        public Mono<ResponseEntity<PoiPlatformStatDTO>> getStatById(
                        @Parameter(description = "ID of the statistic to be retrieved", required = true) @PathVariable("stat_id") UUID statId) {
                return service.getStatById(statId)
                                .map(stat -> ResponseEntity.ok(stat))
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                                .doOnSuccess(response -> log.info("Recherche de statistique par ID: {} - Status: {}",
                                                statId, response.getStatusCode()));
        }

        @Operation(summary = "Get statistics by organization", description = "Retrieves all statistics for a specific organization")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping("/organization/{org_id}/stats")
        public Flux<PoiPlatformStatDTO> getStatsByOrgId(
                        @Parameter(description = "Organization ID to filter statistics", required = true) @PathVariable("org_id") UUID orgId) {
                return service.getStatsByOrgId(orgId)
                                .doOnComplete(() -> log.info("Récupération des statistiques pour l'organisation: {}",
                                                orgId));
        }

        @Operation(summary = "Get statistics by POI", description = "Retrieves all statistics for a specific point of interest")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping("/poi/{poi_id}/stats")
        public Flux<PoiPlatformStatDTO> getStatsByPoiId(
                        @Parameter(description = "POI ID to filter statistics", required = true) @PathVariable("poi_id") UUID poiId) {
                return service.getStatsByPoiId(poiId)
                                .doOnComplete(() -> log.info("Récupération des statistiques pour le POI: {}", poiId));
        }

        @Operation(summary = "Get statistics by platform type", description = "Retrieves all statistics for a specific platform type")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping("/platform/{platform_type}/stats")
        public Flux<PoiPlatformStatDTO> getStatsByPlatformType(
                        @Parameter(description = "Platform type to filter statistics", required = true) @PathVariable("platform_type") String platformType) {
                return service.getStatsByPlatformType(platformType)
                                .doOnComplete(() -> log.info("Récupération des statistiques pour la plateforme: {}",
                                                platformType));
        }

        @Operation(summary = "Get statistics by date", description = "Retrieves all statistics for a specific date")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping("/date/{date}/stats")
        public Flux<PoiPlatformStatDTO> getStatsByDate(
                        @Parameter(description = "Date to filter statistics (format: yyyy-MM-dd)", required = true) @PathVariable LocalDate date) {
                return service.getStatsByDate(date)
                                .doOnComplete(() -> log.info("Récupération des statistiques pour la date: {}", date));
        }

        @Operation(summary = "Get statistics by date range", description = "Retrieves all statistics between two dates")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping("/date-range")
        public Flux<PoiPlatformStatDTO> getStatsByDateRange(
                        @Parameter(description = "Start date of the range (format: yyyy-MM-dd)", required = true) @RequestParam LocalDate startDate,
                        @Parameter(description = "End date of the range (format: yyyy-MM-dd)", required = true) @RequestParam LocalDate endDate) {
                return service.getStatsByDateRange(startDate, endDate)
                                .doOnComplete(() -> log.info("Récupération des statistiques entre {} et {}", startDate,
                                                endDate));
        }

        @Operation(summary = "Get statistics by organization and date range", description = "Retrieves all statistics for a specific organization between two dates")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved statistics", content = @Content(schema = @Schema(implementation = PoiPlatformStatDTO.class)))
        @GetMapping("/organization/{org_id}/date-range")
        public Flux<PoiPlatformStatDTO> getStatsByOrgIdAndDateRange(
                        @Parameter(description = "Organization ID to filter statistics", required = true) @PathVariable("org_id") UUID orgId,
                        @Parameter(description = "Start date of the range (format: yyyy-MM-dd)", required = true) @RequestParam LocalDate startDate,
                        @Parameter(description = "End date of the range (format: yyyy-MM-dd)", required = true) @RequestParam LocalDate endDate) {
                return service.getStatsByOrgIdAndDateRange(orgId, startDate, endDate)
                                .doOnComplete(() -> log.info(
                                                "Récupération des statistiques pour l'organisation {} entre {} et {}",
                                                orgId, startDate, endDate));
        }

        @Operation(summary = "Delete a statistic", description = "Deletes a specific statistic by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Statistic deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Statistic not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/{stat_id}")
        public Mono<ResponseEntity<Void>> deleteStat(
                        @Parameter(description = "ID of the statistic to be deleted", required = true) @PathVariable("stat_id") UUID statId) {
                return service.deleteStat(statId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorResume(error -> {
                                        log.error("Erreur lors de la suppression de la statistique: {}", statId, error);
                                        if (error.getMessage().contains("non trouvée")) {
                                                return Mono.just(ResponseEntity.notFound().build());
                                        }
                                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .build());
                                })
                                .doOnSuccess(response -> log.info("Suppression de la statistique: {} - Status: {}",
                                                statId, response.getStatusCode()));
        }

        @Operation(summary = "Delete statistics by organization", description = "Deletes all statistics for a specific organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Statistics deleted successfully"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/organization/{org_id}")
        public Mono<ResponseEntity<Void>> deleteStatsByOrgId(
                        @Parameter(description = "Organization ID for which to delete statistics", required = true) @PathVariable("org_id") UUID orgId) {
                return service.deleteStatsByOrgId(orgId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorResume(error -> {
                                        log.error("Erreur lors de la suppression des statistiques pour l'organisation: {}",
                                                        orgId, error);
                                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .build());
                                })
                                .doOnSuccess(response -> log.info(
                                                "Suppression des statistiques pour l'organisation: {} - Status: {}",
                                                orgId, response.getStatusCode()));
        }

        @Operation(summary = "Delete statistics by POI", description = "Deletes all statistics for a specific point of interest")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Statistics deleted successfully"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/poi/{poi_id}")
        public Mono<ResponseEntity<Void>> deleteStatsByPoiId(
                        @Parameter(description = "POI ID for which to delete statistics", required = true) @PathVariable("poi_id") UUID poiId) {
                return service.deleteStatsByPoiId(poiId)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorResume(error -> {
                                        log.error("Erreur lors de la suppression des statistiques pour le POI: {}",
                                                        poiId, error);
                                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .build());
                                })
                                .doOnSuccess(response -> log.info(
                                                "Suppression des statistiques pour le POI: {} - Status: {}",
                                                poiId, response.getStatusCode()));
        }
}