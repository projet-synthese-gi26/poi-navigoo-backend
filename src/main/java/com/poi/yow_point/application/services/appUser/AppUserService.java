package com.poi.yow_point.application.services.appUser;

import com.poi.yow_point.presentation.dto.AppUserDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AppUserService {

    /**
     * Crée un nouvel utilisateur
     */
    Mono<AppUserDTO> saveUser(AppUserDTO appUserDTO);

    /**
     * Met à jour un utilisateur existant
     */
    Mono<AppUserDTO> updateUser(UUID id, AppUserDTO appUserDTO);

    /**
     * Récupère un utilisateur par son ID
     */
    Mono<AppUserDTO> getUserById(UUID id);

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     */
    Mono<AppUserDTO> getUserByUsername(String username);

    /**
     * Récupère un utilisateur par son email
     */
    Mono<AppUserDTO> getUserByEmail(String email);

    /**
     * Récupère tous les utilisateurs
     */
    Flux<AppUserDTO> getAllUsers();

    /**
     * Supprime un utilisateur par son ID
     */
    Mono<Void> deleteUser(UUID id);

    /**
     * Vérifie si un utilisateur existe par son ID
     */
    Mono<Boolean> userExists(UUID id);

    /**
     * Vérifie si un nom d'utilisateur existe
     */
    Mono<Boolean> usernameExists(String username);

    /**
     * Vérifie si un email existe
     */
    Mono<Boolean> emailExists(String email);

    /**
     * Récupère les utilisateurs actifs d'une organisation
     */
    Flux<AppUserDTO> getActiveUsersByOrganization(UUID orgId);

    /**
     * Récupère les utilisateurs par rôle
     */
    Flux<AppUserDTO> getUsersByRole(String role);

    /**
     * Compte les utilisateurs actifs d'une organisation
     */
    Mono<Long> countActiveUsersByOrganization(UUID orgId);
}