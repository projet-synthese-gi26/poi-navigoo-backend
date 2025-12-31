package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.presentation.dto.OrganizationDTO;
import com.poi.yow_point.application.services.organization.OrganizationService;
import com.poi.yow_point.application.validation.OrganizationValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organization API", description = "APIs for managing organizations")
public class OrganizationController {

        private final OrganizationService organizationService;
        private final OrganizationValidator validator;

        public OrganizationController(OrganizationService organizationService,
                        OrganizationValidator validator) {
                this.organizationService = organizationService;
                this.validator = validator;
        }

        @PostMapping
        @Operation(summary = "Create a new organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Organization created successfully", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        public Mono<ResponseEntity<OrganizationDTO>> createOrganization(
                        @RequestBody Mono<OrganizationDTO> organizationDTO) {
                return organizationDTO
                                .doOnNext(this::validate)
                                .flatMap(dto -> organizationService.saveOrganization(dto)
                                                .map(savedOrganization -> ResponseEntity.status(HttpStatus.CREATED)
                                                                .body(savedOrganization))
                                                .onErrorResume(WebExchangeBindException.class,
                                                                e -> Mono.just(ResponseEntity.badRequest().build())));
        }

        private void validate(OrganizationDTO dto) {
                BindingResult errors = new BindException(dto, "organizationDTO");
                validator.validate(dto, errors);
                if (errors.hasErrors()) {
                        throw new WebExchangeBindException(null, errors);
                }
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get an organization by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Found the organization", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Organization not found")
        })
        public Mono<ResponseEntity<OrganizationDTO>> getOrganizationById(
                        @Parameter(description = "ID of the organization to be retrieved") @PathVariable UUID id) {
                return organizationService.getOrganizationById(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @GetMapping
        @Operation(summary = "Get all organizations")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of organizations", content = @Content(schema = @Schema(implementation = OrganizationDTO.class)))
        public Flux<OrganizationDTO> getAllOrganizations() {
                return organizationService.getAllOrganizations();
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update an existing organization")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Organization updated successfully", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Organization not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        public Mono<ResponseEntity<OrganizationDTO>> updateOrganization(
                        @Parameter(description = "ID of the organization to be updated") @PathVariable UUID id,
                        @Valid @RequestBody Mono<OrganizationDTO> organizationDTO) {
                return organizationDTO
                                .doOnNext(this::validate)
                                .flatMap(dto -> organizationService.updateOrganization(id, dto))
                                .map(ResponseEntity::ok)
                                .onErrorResume(WebExchangeBindException.class,
                                                e -> Mono.just(ResponseEntity.badRequest().build()))
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete an organization by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Organization deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Organization not found")
        })
        public Mono<ResponseEntity<Void>> deleteOrganization(
                        @Parameter(description = "ID of the organization to be deleted") @PathVariable UUID id) {
                return organizationService.deleteOrganization(id)
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @GetMapping("/by-code/{org_code}")
        @Operation(summary = "Get an organization by its organization code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Found the organization", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Organization not found")
        })
        public Mono<ResponseEntity<OrganizationDTO>> getOrganizationByOrgCode(
                        @Parameter(description = "Organization code") @PathVariable String org_code) {
                return organizationService.getOrganizationByOrgCode(org_code)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @GetMapping("/by-type/{org_type}")
        @Operation(summary = "Get organizations by type")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved organizations by type", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "No organizations found for the specified type")
        })
        public Flux<OrganizationDTO> getOrganizationsByType(
                        @Parameter(description = "Type of the organization") @PathVariable String org_type) {
                return organizationService.getOrganizationsByType(org_type);
        }

        @GetMapping("/by-active-status/{is_active}")
        @Operation(summary = "Get organizations by active status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved organizations by active status", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "No organizations found for the specified active status")
        })
        public Flux<OrganizationDTO> getOrganizationsByActiveStatus(
                        @Parameter(description = "Active status of the organization") @PathVariable Boolean is_active) {
                return organizationService.getOrganizationsByActiveStatus(is_active);
        }

        @GetMapping("/by-name/{org_name}")
        @Operation(summary = "Search organizations by name")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully searched organizations by name", content = @Content(schema = @Schema(implementation = OrganizationDTO.class))),
                        @ApiResponse(responseCode = "404", description = "No organizations found for the specified name")
        })
        public Flux<OrganizationDTO> searchOrganizationsByName(
                        @Parameter(description = "Name of the organization to search for") @PathVariable String org_name) {
                return organizationService.searchOrganizationsByName(org_name);
        }
}