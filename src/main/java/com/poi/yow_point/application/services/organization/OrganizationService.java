package com.poi.yow_point.application.services.organization;

import com.poi.yow_point.presentation.dto.OrganizationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrganizationService {

    Mono<OrganizationDTO> saveOrganization(OrganizationDTO organizationDTO);

    Mono<OrganizationDTO> getOrganizationById(UUID id);

    Flux<OrganizationDTO> getAllOrganizations();

    Mono<OrganizationDTO> updateOrganization(UUID id, OrganizationDTO organizationDTO);

    Mono<Void> deleteOrganization(UUID id);

    Mono<OrganizationDTO> getOrganizationByOrgCode(String orgCode);

    Flux<OrganizationDTO> getOrganizationsByType(String orgType);

    Flux<OrganizationDTO> getOrganizationsByActiveStatus(Boolean isActive);

    Flux<OrganizationDTO> searchOrganizationsByName(String orgName);

}