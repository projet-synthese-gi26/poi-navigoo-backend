package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.services.poiAccessLog.PoiAccessLogService;
import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Map;
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
@RequestMapping("/api/poi-access-logs")
@RequiredArgsConstructor
@Tag(name = "POI Access Logs", description = "API for managing Point of Interest access logs")
public class PoiAccessLogController {

        private final PoiAccessLogService service;

        @Operation(summary = "Create access log", description = "Creates a new access log entry")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Access log created successfully", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        public Mono<PoiAccessLogDTO> createAccessLog(@RequestBody PoiAccessLogDTO dto) {
                log.info("Création d'un nouveau log d'accès pour POI: {}", dto.getPoiId());
                return service.createAccessLog(dto);
        }

        @Operation(summary = "Get access log by ID", description = "Retrieves a specific access log by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Access log found", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Access log not found")
        })
        @GetMapping(value = "/{access_id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<PoiAccessLogDTO> getAccessLogById(
                        @Parameter(description = "ID of the access log to retrieve", required = true) @PathVariable("access_id") UUID accessId) {
                log.info("Récupération du log d'accès: {}", accessId);
                return service.getAccessLogById(accessId);
        }

        @Operation(summary = "Get all access logs", description = "Retrieves all access logs")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        public Flux<PoiAccessLogDTO> getAllAccessLogs() {
                log.info("Récupération de tous les logs d'accès");
                return service.getAllAccessLogs();
        }

        @Operation(summary = "Get access logs by POI", description = "Retrieves all access logs for a specific POI")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/poi/{poi_id}")
        public Flux<PoiAccessLogDTO> getAccessLogsByPoiId(
                        @Parameter(description = "POI ID to filter access logs", required = true) @PathVariable("poi_id") UUID poiId) {
                log.info("Récupération des logs d'accès pour POI: {}", poiId);
                return service.getAccessLogsByPoiId(poiId);
        }

        @Operation(summary = "Get access logs by organization", description = "Retrieves all access logs for a specific organization")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/organization/{organization_id}")
        public Flux<PoiAccessLogDTO> getAccessLogsByOrganizationId(
                        @Parameter(description = "Organization ID to filter access logs", required = true) @PathVariable("organization_id") UUID organizationId) {
                log.info("Récupération des logs d'accès pour organisation: {}", organizationId);
                return service.getAccessLogsByOrganizationId(organizationId);
        }

        @Operation(summary = "Get access logs by user", description = "Retrieves all access logs for a specific user")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/user/{user_id}")
        public Flux<PoiAccessLogDTO> getAccessLogsByUserId(
                        @Parameter(description = "User ID to filter access logs", required = true) @PathVariable("user_id") UUID userId) {
                log.info("Récupération des logs d'accès pour utilisateur: {}", userId);
                return service.getAccessLogsByUserId(userId);
        }

        @Operation(summary = "Get access logs by access type", description = "Retrieves all access logs for a specific access type")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/access-type/{access_type}")
        public Flux<PoiAccessLogDTO> getAccessLogsByAccessType(
                        @Parameter(description = "Access type to filter logs (e.g., 'entry', 'exit')", required = true) @PathVariable("access_type") String accessType) {
                log.info("Récupération des logs d'accès pour type: {}", accessType);
                return service.getAccessLogsByAccessType(accessType);
        }

        @Operation(summary = "Get access logs by platform", description = "Retrieves all access logs for a specific platform")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/platform/{platform_type}")
        public Flux<PoiAccessLogDTO> getAccessLogsByPlatformType(
                        @Parameter(description = "Platform type to filter logs (e.g., 'mobile', 'web')", required = true) @PathVariable("platform_type") String platformType) {
                log.info("Récupération des logs d'accès pour plateforme: {}", platformType);
                return service.getAccessLogsByPlatformType(platformType);
        }

        @Operation(summary = "Get access logs by POI and organization", description = "Retrieves all access logs for a specific POI and organization")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/poi/{poi_id}/organization/{organization_id}")
        public Flux<PoiAccessLogDTO> getAccessLogsByPoiAndOrganization(
                        @Parameter(description = "POI ID to filter access logs", required = true) @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "Organization ID to filter access logs", required = true) @PathVariable("organization_id") UUID organizationId) {
                log.info("Récupération des logs d'accès pour POI: {} et organisation: {}", poiId, organizationId);
                return service.getAccessLogsByPoiAndOrganization(poiId, organizationId);
        }

        @Operation(summary = "Get access logs by date range", description = "Retrieves all access logs between two dates")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/date-range")
        public Flux<PoiAccessLogDTO> getAccessLogsByDateRange(
                        @Parameter(description = "Start date of the range (ISO format)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
                        @Parameter(description = "End date of the range (ISO format)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
                log.info("Récupération des logs d'accès entre {} et {}", startDate, endDate);
                return service.getAccessLogsByDateRange(startDate, endDate);
        }

        @Operation(summary = "Get recent access logs by POI", description = "Retrieves recent access logs for a specific POI since a given date")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/poi/{poi_id}/recent")
        public Flux<PoiAccessLogDTO> getRecentAccessLogsByPoiId(
                        @Parameter(description = "POI ID to filter access logs", required = true) @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "Date from which to retrieve logs (ISO format)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime since) {
                log.info("Récupération des logs d'accès récents pour POI: {} depuis {}", poiId, since);
                return service.getRecentAccessLogsByPoiId(poiId, since);
        }

        @Operation(summary = "Get paginated access logs by POI", description = "Retrieves access logs for a specific POI with pagination")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved access logs", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class)))
        @GetMapping("/poi/{poi_id}/paginated")
        public Flux<PoiAccessLogDTO> getAccessLogsByPoiIdWithPagination(
                        @Parameter(description = "POI ID to filter access logs", required = true) @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size) {
                log.info("Récupération paginée des logs d'accès pour POI: {} (page: {}, taille: {})", poiId, page,
                                size);
                return service.getAccessLogsByPoiIdWithPagination(poiId, page, size);
        }

        /*
         * @Operation(summary = "Count access logs by POI", description =
         * "Counts all access logs for a specific POI")
         * 
         * @ApiResponses(value = {
         * 
         * @ApiResponse(responseCode = "200", description =
         * "Count retrieved successfully", content = @Content(schema
         * = @Schema(implementation = Long.class))),
         * 
         * @ApiResponse(responseCode = "500", description = "Internal server error")
         * })
         * 
         * @GetMapping("/poi/{poi_id}/count")
         * public Mono<ResponseEntity<Long>> countAccessLogsByPoiId(
         * 
         * @Parameter(description = "POI ID to count access logs", required =
         * true) @PathVariable UUID poiId) {
         * log.info("Comptage des accès pour POI: {}", poiId);
         * 
         * return service.countAccessLogsByPoiId(poiId)
         * .map(count -> ResponseEntity.ok(count))
         * .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(
         * ));
         * }
         */

        @Operation(summary = "Count access logs by POI and access type", description = "Counts all access logs for a specific POI and access type")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content(schema = @Schema(implementation = Long.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/poi/{poi_id}/count/access-type/{access_type}")
        public Mono<ResponseEntity<Long>> countAccessLogsByPoiIdAndAccessType(
                        @Parameter(description = "POI ID to count access logs", required = true) @PathVariable("poi_id") UUID poiId,
                        @Parameter(description = "Access type to filter count (e.g., 'entry', 'exit')", required = true) @PathVariable("access_type") String accessType) {
                log.info("Comptage des accès de type {} pour POI: {}", accessType, poiId);

                return service.countAccessLogsByPoiIdAndAccessType(poiId, accessType)
                                .map(count -> ResponseEntity.ok(count))
                                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }

        @Operation(summary = "Get platform statistics by organization", description = "Retrieves platform statistics (count by platform) for a specific organization")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved platform statistics", content = @Content(schema = @Schema(implementation = Map.class)))
        @GetMapping("/organization/{organization_id}/platform-stats")
        public Flux<Map<String, Object>> getPlatformStatsForOrganization(
                        @Parameter(description = "Organization ID to get platform statistics", required = true) @PathVariable("organization_id") UUID organizationId) {
                log.info("Récupération des statistiques par plateforme pour organisation: {}", organizationId);
                return service.getPlatformStatsForOrganization(organizationId);
        }

        @Operation(summary = "Update access log", description = "Updates an existing access log")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Access log updated successfully", content = @Content(schema = @Schema(implementation = PoiAccessLogDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Access log not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PutMapping("/{access_id}")
        public Mono<ResponseEntity<PoiAccessLogDTO>> updateAccessLog(
                        @Parameter(description = "ID of the access log to update", required = true) @PathVariable("access_id") UUID accessId,
                        @RequestBody PoiAccessLogDTO dto) {
                log.info("Mise à jour du log d'accès: {}", accessId);

                return service.updateAccessLog(accessId, dto)
                                .map(result -> ResponseEntity.ok(result))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Delete access log", description = "Deletes a specific access log by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Access log deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Access log not found")
        })
        @DeleteMapping("/{access_id}")
        public Mono<ResponseEntity<Void>> deleteAccessLog(
                        @Parameter(description = "ID of the access log to delete", required = true) @PathVariable("access_id") UUID accessId) {
                log.info("Suppression du log d'accès: {}", accessId);

                return service.deleteAccessLog(accessId)
                                .map(result -> ResponseEntity.noContent().<Void>build())
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Delete old access logs", description = "Deletes all access logs older than a specified date")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Old access logs deleted successfully", content = @Content(schema = @Schema(implementation = Map.class))),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/cleanup")
        public Mono<ResponseEntity<Map<String, Object>>> deleteOldLogs(
                        @Parameter(description = "Delete logs older than this date (ISO format)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime beforeDate) {
                log.info("Suppression des logs d'accès antérieurs à: {}", beforeDate);

                return service.deleteOldLogs(beforeDate)
                                .map(count -> {
                                        Map<String, Object> response = Map.of(
                                                        "deletedCount", count,
                                                        "beforeDate", beforeDate);
                                        return ResponseEntity.ok(response);
                                })
                                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
}