package com.poi.yow_point.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.poi.yow_point.application.services.appUser.AppUserService;
//import com.poi.yow_point.application.services.appUser.AppUserServiceImpl;
import com.poi.yow_point.presentation.dto.AppUserDTO;

import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "APIs for managing application users")
@Validated
public class AppUserController {

        private static final Logger log = LoggerFactory.getLogger(AppUserController.class);
        private final AppUserService appUserService;

        @Autowired
        public AppUserController(AppUserService appUserService) {
                this.appUserService = appUserService;
        }

        @PostMapping
        @Operation(summary = "Create a new user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input"),
                        @ApiResponse(responseCode = "409", description = "Username or email already exists")
        })
        public Mono<ResponseEntity<AppUserDTO>> createUser(@Valid @RequestBody AppUserDTO appUserDTO) {
                log.info("Received request to create user: {}", appUserDTO.getUsername());

                return appUserService.saveUser(appUserDTO)
                                .doOnSuccess(savedUser -> log.info("User created with ID: {}", savedUser.getUserId()))
                                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
                                .onErrorResume(IllegalArgumentException.class,
                                                ex -> Mono.just(ResponseEntity.badRequest().build()))
                                .onErrorResume(Exception.class,
                                                ex -> {
                                                        log.error("Error creating user: {}", ex.getMessage());
                                                        return Mono.just(ResponseEntity
                                                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                        .build());
                                                });
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get a user by their ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Found the user", content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public Mono<ResponseEntity<AppUserDTO>> getUserById(
                        @Parameter(description = "ID of the user to be retrieved") @PathVariable UUID id) {
                log.info("Received request to get user by ID: {}", id);

                return appUserService.getUserById(id)
                                .doOnSuccess(user -> log.info("Found user: {}", user.getUsername()))
                                .map(ResponseEntity::ok)
                                .switchIfEmpty(Mono.fromSupplier(() -> {
                                        log.warn("User not found for ID: {}", id);
                                        return ResponseEntity.notFound().build();
                                }));
        }

        @GetMapping("/username/{username}")
        @Operation(summary = "Get a user by their username")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Found the user", content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public Mono<ResponseEntity<AppUserDTO>> getUserByUsername(
                        @Parameter(description = "Username of the user to be retrieved") @PathVariable @NotBlank String username) {
                log.info("Received request to get user by username: {}", username);

                return appUserService.getUserByUsername(username)
                                .doOnSuccess(user -> log.info("Found user: {}", user.getUsername()))
                                .map(ResponseEntity::ok)
                                .switchIfEmpty(Mono.fromSupplier(() -> {
                                        log.warn("User not found for username: {}", username);
                                        return ResponseEntity.notFound().build();
                                }));
        }

        @GetMapping("/email/{email}")
        @Operation(summary = "Get a user by their email address")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Found the user", content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public Mono<ResponseEntity<AppUserDTO>> getUserByEmail(
                        @Parameter(description = "Email address of the user to be retrieved") @PathVariable @Email String email) {
                log.info("Received request to get user by email: {}", email);

                return appUserService.getUserByEmail(email)
                                .doOnSuccess(user -> log.info("Found user: {}", user.getUsername()))
                                .map(ResponseEntity::ok)
                                .switchIfEmpty(Mono.fromSupplier(() -> {
                                        log.warn("User not found for email: {}", email);
                                        return ResponseEntity.notFound().build();
                                }));
        }

        @GetMapping
        @Operation(summary = "Get all users")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users", content = @Content(schema = @Schema(implementation = AppUserDTO.class)))
        public Flux<AppUserDTO> getAllUsers() {
                log.info("Received request to get all users");
                return appUserService.getAllUsers()
                                .doOnComplete(() -> log.info("Completed fetching all users"));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update an existing user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        public Mono<ResponseEntity<AppUserDTO>> updateUser(
                        @Parameter(description = "ID of the user to be updated") @PathVariable UUID id,
                        @Valid @RequestBody AppUserDTO userDetailsDTO) {
                log.info("Received request to update user with ID: {}", id);

                return appUserService.updateUser(id, userDetailsDTO)
                                .doOnSuccess(updatedUser -> log.info("User updated with ID: {}",
                                                updatedUser.getUserId()))
                                .map(ResponseEntity::ok)
                                .onErrorResume(IllegalArgumentException.class,
                                                ex -> Mono.just(ResponseEntity.badRequest().build()))
                                .onErrorResume(RuntimeException.class,
                                                ex -> Mono.just(ResponseEntity.notFound().build()))
                                .doOnError(error -> log.warn("Failed to update user with ID: {}. Reason: {}", id,
                                                error.getMessage()));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a user by their ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public Mono<ResponseEntity<Void>> deleteUser(
                        @Parameter(description = "ID of the user to be deleted") @PathVariable UUID id) {
                log.info("Received request to delete user by ID: {}", id);

                return appUserService.deleteUser(id)
                                .doOnSuccess(unused -> log.info("User deleted with ID: {}", id))
                                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                                .onErrorReturn(ResponseEntity.notFound().build());
        }

        // Endpoints utilitaires
        @GetMapping("/{id}/exists")
        @Operation(summary = "Check if a user exists by ID")
        @ApiResponse(responseCode = "200", description = "Returns true if user exists, false otherwise")
        public Mono<ResponseEntity<Boolean>> userExists(@PathVariable UUID id) {
                return appUserService.userExists(id)
                                .map(ResponseEntity::ok);
        }

        @GetMapping("/check-username/{username}")
        @Operation(summary = "Check if a username is already taken")
        @ApiResponse(responseCode = "200", description = "Returns true if username exists, false otherwise")
        public Mono<ResponseEntity<Boolean>> usernameExists(@PathVariable @NotBlank String username) {
                return appUserService.usernameExists(username)
                                .map(ResponseEntity::ok);
        }

        @GetMapping("/check-email/{email}")
        @Operation(summary = "Check if an email is already registered")
        @ApiResponse(responseCode = "200", description = "Returns true if email exists, false otherwise")
        public Mono<ResponseEntity<Boolean>> emailExists(@PathVariable @Email String email) {
                return appUserService.emailExists(email)
                                .map(ResponseEntity::ok);
        }

        // Nouveaux endpoints pour les fonctionnalités étendues
        @GetMapping("/organization/{org_id}/active")
        @Operation(summary = "Get active users by organization")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active users for organization")
        public Flux<AppUserDTO> getActiveUsersByOrganization(@PathVariable("org_id") UUID orgId) {
                log.info("Received request to get active users for organization: {}", orgId);
                return appUserService.getActiveUsersByOrganization(orgId)
                                .doOnComplete(() -> log.info("Completed fetching active users for organization: {}",
                                                orgId));
        }

        @GetMapping("/role/{role}")
        @Operation(summary = "Get users by role")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users with specified role")
        public Flux<AppUserDTO> getUsersByRole(@PathVariable @NotBlank String role) {
                log.info("Received request to get users with role: {}", role);
                return appUserService.getUsersByRole(role)
                                .doOnComplete(() -> log.info("Completed fetching users with role: {}", role));
        }

        @GetMapping("/organization/{orgId}/count")
        @Operation(summary = "Count active users in organization")
        @ApiResponse(responseCode = "200", description = "Returns count of active users in organization")
        public Mono<ResponseEntity<Long>> countActiveUsersByOrganization(@PathVariable UUID orgId) {
                return appUserService.countActiveUsersByOrganization(orgId)
                                .map(ResponseEntity::ok);
        }
}