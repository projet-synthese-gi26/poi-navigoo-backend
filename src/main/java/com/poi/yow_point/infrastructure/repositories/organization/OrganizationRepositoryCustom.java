package com.poi.yow_point.infrastructure.repositories.organization;

import com.poi.yow_point.infrastructure.entities.Organization;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrganizationRepositoryCustom {

    Flux<Organization> findAllActive();

    // Méthode personnalisée pour rechercher par orgCode
    Mono<Organization> findByOrgCode(String orgCode);

    // Méthode pour rechercher par type d'organisation
    Flux<Organization> findByOrgType(String orgType);

    // Méthode pour rechercher par statut actif
    Flux<Organization> findByIsActive(Boolean isActive);

    // Méthode pour rechercher par nom contenant
    Flux<Organization> findByOrgName(String orgName);

    // Méthode pour rechercher par type et statut
    Flux<Organization> findByOrgTypeAndIsActive(String orgType, Boolean isActive);
}
