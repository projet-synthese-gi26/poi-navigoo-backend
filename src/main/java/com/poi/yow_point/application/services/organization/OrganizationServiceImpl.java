package com.poi.yow_point.application.services.organization;

import com.poi.yow_point.application.mappers.OrganizationMapper;
//import com.poi.yow_point.application.validation.OrganizationValidator;
import com.poi.yow_point.infrastructure.repositories.organization.OrganizationRepository;
import com.poi.yow_point.presentation.dto.OrganizationDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
            OrganizationMapper organizationMapper) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
    }

    @Override
    @Transactional
    public Mono<OrganizationDTO> saveOrganization(OrganizationDTO organizationDTO) {
        return Mono.just(organizationMapper.toEntity(organizationDTO))
                .flatMap(organizationRepository::save)
                .doOnNext(savedOrg -> log.info("Saved organization with ID: {}", savedOrg.getOrganizationId()))
                .map(organizationMapper::toDTO)
                .doOnError(error -> log.error("Error saving organization: {}", error.getMessage()));
    }

    @Override
    public Mono<OrganizationDTO> getOrganizationById(UUID id) {
        return organizationRepository.findById(id)
                .map(organizationMapper::toDTO)
                .doOnError(error -> log.error("Error fetching organization by ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Flux<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll()
                .map(organizationMapper::toDTO)
                .doOnError(error -> log.error("Error fetching all organizations: {}", error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<OrganizationDTO> updateOrganization(UUID id, OrganizationDTO organizationDTO) {
        return organizationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Organization not found with id " + id)))
                .flatMap(existingOrg -> {
                    organizationMapper.updateFromDto(organizationDTO, existingOrg);
                    return organizationRepository.save(existingOrg);
                })
                .map(organizationMapper::toDTO)
                .doOnError(error -> log.error("Error updating organization with ID {}: {}", id, error.getMessage()));
    }

    @Override
    @Transactional
    public Mono<Void> deleteOrganization(UUID id) {
        return organizationRepository.deleteById(id)
                .doOnError(error -> log.error("Error deleting organization with ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<OrganizationDTO> getOrganizationByOrgCode(String orgCode) {
        return organizationRepository.findByOrgCode(orgCode)
                .map(organizationMapper::toDTO)
                .doOnError(error -> log.error("Error fetching organization by orgCode {}: {}", orgCode,
                        error.getMessage()));
    }

    @Override
    public Flux<OrganizationDTO> getOrganizationsByType(String orgType) {
        log.info("Fetching organizations by type: {}", orgType);

        return organizationRepository.findByOrgType(orgType)
                .map(organizationMapper::toDTO)
                .doOnComplete(() -> log.info("Completed fetching organizations by type: {}", orgType))
                .doOnError(
                        error -> log.error("Error fetching organizations by type {}: {}", orgType, error.getMessage()));
    }

    @Override
    public Flux<OrganizationDTO> getOrganizationsByActiveStatus(Boolean isActive) {
        log.info("Fetching organizations by active status: {}", isActive);

        return organizationRepository.findByIsActive(isActive)
                .map(organizationMapper::toDTO)
                .doOnComplete(() -> log.info("Completed fetching organizations by active status: {}", isActive))
                .doOnError(error -> log.error("Error fetching organizations by active status {}: {}", isActive,
                        error.getMessage()));
    }

    @Override
    public Flux<OrganizationDTO> searchOrganizationsByName(String orgName) {
        log.info("Searching organizations by name: {}", orgName);

        return organizationRepository.findByOrgName(orgName)
                .map(organizationMapper::toDTO)
                .doOnComplete(() -> log.info("Completed searching organizations by name: {}", orgName))
                .doOnError(error -> log.error("Error searching organizations by name {}: {}", orgName,
                        error.getMessage()));
    }
}