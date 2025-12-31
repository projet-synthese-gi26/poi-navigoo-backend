package com.poi.yow_point.application.services.appUser;

import com.poi.yow_point.application.mappers.AppUserMapper;
import com.poi.yow_point.application.validation.AppUserValidator;
import com.poi.yow_point.infrastructure.entities.AppUser;
//import com.poi.yow_point.infrastructure.repositories.OrganizationRepository;
import com.poi.yow_point.infrastructure.repositories.appUser.AppUserRepository;
import com.poi.yow_point.presentation.dto.AppUserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class AppUserServiceImpl implements AppUserService {

    private static final Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final AppUserValidator validationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserServiceImpl(AppUserRepository appUserRepository,
            AppUserMapper appUserMapper,
            AppUserValidator validationService,
            PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.validationService = validationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Mono<AppUserDTO> saveUser(AppUserDTO appUserDTO) {
        log.info("Saving user: {}", appUserDTO.getUsername());

        return validationService.validateForCreation(appUserDTO)
                .then(Mono.fromCallable(() -> {
                    // Hasher le mot de passe
                    String hashedPassword = passwordEncoder.encode(appUserDTO.getPassword());

                    // Créer l'entité avec le mot de passe hashé
                    AppUser appUser = appUserMapper.toEntity(appUserDTO);
                    appUser.setPasswordHash(hashedPassword);

                    return appUser;
                }))
                .flatMap(appUserRepository::save)
                .doOnSuccess(savedUser -> log.info("Saved user with ID: {}", savedUser.getUserId()))
                .map(appUserMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<AppUserDTO> updateUser(UUID id, AppUserDTO appUserDTO) {
        log.info("Updating user with ID: {}", id);

        return validationService.validateForUpdate(id, appUserDTO)
                .then(appUserRepository.findById(id))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with id " + id)))
                .flatMap(existingUser -> {
                    // Mettre à jour les champs
                    appUserMapper.updateFromDto(appUserDTO, existingUser);

                    // Hasher le nouveau mot de passe si fourni
                    if (appUserDTO.getPassword() != null && !appUserDTO.getPassword().trim().isEmpty()) {
                        String hashedPassword = passwordEncoder.encode(appUserDTO.getPassword());
                        existingUser.setPasswordHash(hashedPassword);
                    }

                    return appUserRepository.save(existingUser);
                })
                .doOnSuccess(updatedUser -> log.info("Updated user with ID: {}", updatedUser.getUserId()))
                .map(appUserMapper::toDTO);
    }

    @Override
    public Mono<AppUserDTO> getUserById(UUID id) {
        log.info("Fetching user by ID: {}", id);
        return appUserRepository.findById(id)
                .map(appUserMapper::toDTO);
    }

    @Override
    public Mono<AppUserDTO> getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return appUserRepository.findByUsername(username)
                .map(appUserMapper::toDTO);
    }

    @Override
    public Mono<AppUserDTO> getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return appUserRepository.findByEmail(email)
                .map(appUserMapper::toDTO);
    }

    @Override
    public Flux<AppUserDTO> getAllUsers() {
        log.info("Fetching all users");
        return appUserRepository.findAll()
                .doOnComplete(() -> log.info("Fetched all users"))
                .map(appUserMapper::toDTO);
    }

    @Override
    @Transactional
    public Mono<Void> deleteUser(UUID id) {
        log.info("Deleting user by ID: {}", id);
        return appUserRepository.deleteById(id)
                .doOnSuccess(unused -> log.info("Deleted user with ID: {}", id));
    }

    @Override
    public Mono<Boolean> userExists(UUID id) {
        return appUserRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> usernameExists(String username) {
        return appUserRepository.findByUsername(username)
                .hasElement();
    }

    @Override
    public Mono<Boolean> emailExists(String email) {
        return appUserRepository.findByEmail(email)
                .hasElement();
    }

    @Override
    public Flux<AppUserDTO> getActiveUsersByOrganization(UUID orgId) {
        log.info("Fetching active users for organization: {}", orgId);
        return appUserRepository.findByOrgIdAndIsActive(orgId, true)
                .map(appUserMapper::toDTO);
    }

    @Override
    public Flux<AppUserDTO> getUsersByRole(String role) {
        log.info("Fetching users with role: {}", role);
        return appUserRepository.findByRole(role)
                .map(appUserMapper::toDTO);
    }

    @Override
    public Mono<Long> countActiveUsersByOrganization(UUID orgId) {
        return appUserRepository.countActiveUsersByOrgId(orgId);
    }
}