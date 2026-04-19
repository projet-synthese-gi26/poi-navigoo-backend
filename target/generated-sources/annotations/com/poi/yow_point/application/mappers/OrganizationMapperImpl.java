package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Organization;
import com.poi.yow_point.presentation.dto.OrganizationDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T17:19:59+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public OrganizationDTO toDTO(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationDTO.OrganizationDTOBuilder organizationDTO = OrganizationDTO.builder();

        organizationDTO.createdAt( organization.getCreatedAt() );
        organizationDTO.isActive( organization.getIsActive() );
        organizationDTO.orgCode( organization.getOrgCode() );
        organizationDTO.orgType( organization.getOrgType() );
        organizationDTO.organizationId( organization.getOrganizationId() );
        organizationDTO.organizationName( organization.getOrganizationName() );

        return organizationDTO.build();
    }

    @Override
    public Organization toEntity(OrganizationDTO organizationDTO) {
        if ( organizationDTO == null ) {
            return null;
        }

        Organization.OrganizationBuilder organization = Organization.builder();

        organization.isActive( organizationDTO.getIsActive() );
        organization.orgCode( organizationDTO.getOrgCode() );
        organization.orgType( organizationDTO.getOrgType() );
        organization.organizationName( organizationDTO.getOrganizationName() );

        return organization.build();
    }

    @Override
    public void updateFromDto(OrganizationDTO dto, Organization entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getOrgCode() != null ) {
            entity.setOrgCode( dto.getOrgCode() );
        }
        if ( dto.getOrgType() != null ) {
            entity.setOrgType( dto.getOrgType() );
        }
        if ( dto.getOrganizationName() != null ) {
            entity.setOrganizationName( dto.getOrganizationName() );
        }
    }
}
