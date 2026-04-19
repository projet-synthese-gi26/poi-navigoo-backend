package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.presentation.dto.AppUserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T17:19:59+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class AppUserMapperImpl implements AppUserMapper {

    @Override
    public AppUserDTO toDTO(AppUser appUser) {
        if ( appUser == null ) {
            return null;
        }

        AppUserDTO.AppUserDTOBuilder appUserDTO = AppUserDTO.builder();

        appUserDTO.createdAt( appUser.getCreatedAt() );
        appUserDTO.email( appUser.getEmail() );
        appUserDTO.isActive( appUser.getIsActive() );
        appUserDTO.organizationId( appUser.getOrganizationId() );
        appUserDTO.phone( appUser.getPhone() );
        appUserDTO.role( appUser.getRole() );
        appUserDTO.userId( appUser.getUserId() );
        appUserDTO.username( appUser.getUsername() );

        return appUserDTO.build();
    }

    @Override
    public AppUser toEntity(AppUserDTO appUserDTO) {
        if ( appUserDTO == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.email( appUserDTO.getEmail() );
        appUser.isActive( appUserDTO.getIsActive() );
        appUser.organizationId( appUserDTO.getOrganizationId() );
        appUser.phone( appUserDTO.getPhone() );
        appUser.role( appUserDTO.getRole() );
        appUser.username( appUserDTO.getUsername() );

        return appUser.build();
    }

    @Override
    public void updateFromDto(AppUserDTO dto, AppUser entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getEmail() != null ) {
            entity.setEmail( dto.getEmail() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getOrganizationId() != null ) {
            entity.setOrganizationId( dto.getOrganizationId() );
        }
        if ( dto.getPhone() != null ) {
            entity.setPhone( dto.getPhone() );
        }
        if ( dto.getRole() != null ) {
            entity.setRole( dto.getRole() );
        }
        if ( dto.getUsername() != null ) {
            entity.setUsername( dto.getUsername() );
        }
    }

    @Override
    public AppUser toEntityWithPassword(AppUserDTO appUserDTO, String passwordHash) {
        if ( appUserDTO == null && passwordHash == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        if ( appUserDTO != null ) {
            appUser.email( appUserDTO.getEmail() );
            appUser.isActive( appUserDTO.getIsActive() );
            appUser.organizationId( appUserDTO.getOrganizationId() );
            appUser.phone( appUserDTO.getPhone() );
            appUser.role( appUserDTO.getRole() );
            appUser.username( appUserDTO.getUsername() );
        }
        appUser.passwordHash( passwordHash );

        return appUser.build();
    }
}
