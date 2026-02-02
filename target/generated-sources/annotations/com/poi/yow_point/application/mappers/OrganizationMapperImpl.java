package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Organization;
import com.poi.yow_point.presentation.dto.OrganizationDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-02T13:13:53+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public OrganizationDTO toDTO(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationDTO.OrganizationDTOBuilder organizationDTO = OrganizationDTO.builder();

        organizationDTO.organizationId( organization.getOrganizationId() );
        organizationDTO.organizationName( organization.getOrganizationName() );
        organizationDTO.orgCode( organization.getOrgCode() );
        organizationDTO.orgType( organization.getOrgType() );
        organizationDTO.createdAt( organization.getCreatedAt() );
        organizationDTO.isActive( organization.getIsActive() );

        return organizationDTO.build();
    }

    @Override
    public Organization toEntity(OrganizationDTO organizationDTO) {
        if ( organizationDTO == null ) {
            return null;
        }

        Organization.OrganizationBuilder organization = Organization.builder();

        organization.organizationName( organizationDTO.getOrganizationName() );
        organization.orgCode( organizationDTO.getOrgCode() );
        organization.orgType( organizationDTO.getOrgType() );
        organization.isActive( organizationDTO.getIsActive() );

        return organization.build();
    }

    @Override
    public void updateFromDto(OrganizationDTO dto, Organization entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getOrganizationName() != null ) {
            entity.setOrganizationName( dto.getOrganizationName() );
        }
        if ( dto.getOrgCode() != null ) {
            entity.setOrgCode( dto.getOrgCode() );
        }
        if ( dto.getOrgType() != null ) {
            entity.setOrgType( dto.getOrgType() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
    }
}
