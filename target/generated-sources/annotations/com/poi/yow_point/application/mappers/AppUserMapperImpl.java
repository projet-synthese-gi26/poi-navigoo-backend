package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.presentation.dto.AppUserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T22:23:27+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class AppUserMapperImpl implements AppUserMapper {

    @Override
    public AppUserDTO toDTO(AppUser appUser) {
        if ( appUser == null ) {
            return null;
        }

        AppUserDTO.AppUserDTOBuilder appUserDTO = AppUserDTO.builder();

        appUserDTO.userId( appUser.getUserId() );
        appUserDTO.organizationId( appUser.getOrganizationId() );
        appUserDTO.username( appUser.getUsername() );
        appUserDTO.email( appUser.getEmail() );
        appUserDTO.phone( appUser.getPhone() );
        appUserDTO.role( appUser.getRole() );
        appUserDTO.isActive( appUser.getIsActive() );
        appUserDTO.createdAt( appUser.getCreatedAt() );

        return appUserDTO.build();
    }

    @Override
    public AppUser toEntity(AppUserDTO appUserDTO) {
        if ( appUserDTO == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.organizationId( appUserDTO.getOrganizationId() );
        appUser.username( appUserDTO.getUsername() );
        appUser.email( appUserDTO.getEmail() );
        appUser.phone( appUserDTO.getPhone() );
        appUser.role( appUserDTO.getRole() );
        appUser.isActive( appUserDTO.getIsActive() );

        return appUser.build();
    }

    @Override
    public void updateFromDto(AppUserDTO dto, AppUser entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getOrganizationId() != null ) {
            entity.setOrganizationId( dto.getOrganizationId() );
        }
        if ( dto.getUsername() != null ) {
            entity.setUsername( dto.getUsername() );
        }
        if ( dto.getEmail() != null ) {
            entity.setEmail( dto.getEmail() );
        }
        if ( dto.getPhone() != null ) {
            entity.setPhone( dto.getPhone() );
        }
        if ( dto.getRole() != null ) {
            entity.setRole( dto.getRole() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
    }

    @Override
    public AppUser toEntityWithPassword(AppUserDTO appUserDTO, String passwordHash) {
        if ( appUserDTO == null && passwordHash == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        if ( appUserDTO != null ) {
            appUser.organizationId( appUserDTO.getOrganizationId() );
            appUser.username( appUserDTO.getUsername() );
            appUser.email( appUserDTO.getEmail() );
            appUser.phone( appUserDTO.getPhone() );
            appUser.role( appUserDTO.getRole() );
            appUser.isActive( appUserDTO.getIsActive() );
        }
        appUser.passwordHash( passwordHash );

        return appUser.build();
    }
}
