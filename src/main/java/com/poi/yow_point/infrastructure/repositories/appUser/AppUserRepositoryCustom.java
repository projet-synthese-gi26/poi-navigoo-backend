package com.poi.yow_point.infrastructure.repositories.appUser;

import java.util.UUID;

import com.poi.yow_point.infrastructure.entities.AppUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppUserRepositoryCustom {
    Mono<AppUser> findByUsername(String username);

    Mono<AppUser> findByEmail(String email);

    Mono<Boolean> existsByOrgId(UUID orgId);

    Mono<Long> countActiveUsersByOrgId(UUID orgId);

    Flux<AppUser> findByOrgIdAndIsActive(UUID orgId, Boolean isActive);

    Flux<AppUser> findByRole(String role);
}
