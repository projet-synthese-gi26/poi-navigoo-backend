package com.poi.yow_point.application.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poi.yow_point.infrastructure.repositories.appUser.AppUserRepository;
import com.poi.yow_point.infrastructure.repositories.organization.OrganizationRepository;
import com.poi.yow_point.presentation.dto.AppUserDTO;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AppUserValidator {

    private final AppUserRepository appUserRepository;
    private final OrganizationRepository organizationRepository;

    @Autowired
    public AppUserValidator(AppUserRepository appUserRepository,
            OrganizationRepository organizationRepository) {
        this.appUserRepository = appUserRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Valide la création d'un nouvel utilisateur
     */
    public Mono<Void> validateForCreation(AppUserDTO userDTO) {
        return Mono.when(
                validateOrganizationExists(userDTO.getOrganizationId()),
                validateUsernameUnique(userDTO.getUsername()),
                validateEmailUnique(userDTO.getEmail()),
                validatePasswordPresent(userDTO.getPassword()));
    }

    /**
     * Valide la mise à jour d'un utilisateur existant
     */
    public Mono<Void> validateForUpdate(UUID userId, AppUserDTO userDTO) {
        return Mono.when(
                validateUserExists(userId),
                validateOrganizationExists(userDTO.getOrganizationId()),
                validateUsernameUniqueForUpdate(userId, userDTO.getUsername()),
                validateEmailUniqueForUpdate(userId, userDTO.getEmail()));
    }

    private Mono<Void> validateOrganizationExists(UUID orgId) {
        if (orgId == null) {
            return Mono.error(new IllegalArgumentException("Organization ID is required"));
        }

        return organizationRepository.existsById(orgId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Organization not found with ID: " + orgId));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateUserExists(UUID userId) {
        return appUserRepository.existsById(userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("User not found with ID: " + userId));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateUsernameUnique(String username) {
        return appUserRepository.findByUsername(username)
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Username already exists: " + username));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateEmailUnique(String email) {
        return appUserRepository.findByEmail(email)
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already exists: " + email));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateUsernameUniqueForUpdate(UUID userId, String username) {
        return appUserRepository.findByUsername(username)
                .filter(user -> !user.getUserId().equals(userId))
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Username already exists: " + username));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateEmailUniqueForUpdate(UUID userId, String email) {
        return appUserRepository.findByEmail(email)
                .filter(user -> !user.getUserId().equals(userId))
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email already exists: " + email));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validatePasswordPresent(String password) {
        if (password == null || password.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Password is required for user creation"));
        }
        return Mono.empty();
    }
}