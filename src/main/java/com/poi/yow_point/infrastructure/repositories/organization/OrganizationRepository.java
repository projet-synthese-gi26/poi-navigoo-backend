package com.poi.yow_point.infrastructure.repositories.organization;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.Organization;

import java.util.UUID;

public interface OrganizationRepository extends R2dbcRepository<Organization, UUID>, OrganizationRepositoryCustom {

}