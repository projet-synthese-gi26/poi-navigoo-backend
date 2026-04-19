package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.presentation.dto.user.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T21:00:14+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(AppUser user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.createdAt( user.getCreatedAt() );
        userDTO.email( user.getEmail() );
        userDTO.emailVerified( user.getEmailVerified() );
        userDTO.isActive( user.getIsActive() );
        userDTO.lastLoginAt( user.getLastLoginAt() );
        userDTO.organizationId( user.getOrganizationId() );
        userDTO.phone( user.getPhone() );
        userDTO.role( user.getRole() );
        userDTO.userId( user.getUserId() );
        userDTO.username( user.getUsername() );

        return userDTO.build();
    }

    @Override
    public AppUser toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.createdAt( userDTO.getCreatedAt() );
        appUser.email( userDTO.getEmail() );
        appUser.emailVerified( userDTO.getEmailVerified() );
        appUser.isActive( userDTO.getIsActive() );
        appUser.lastLoginAt( userDTO.getLastLoginAt() );
        appUser.organizationId( userDTO.getOrganizationId() );
        appUser.phone( userDTO.getPhone() );
        appUser.role( userDTO.getRole() );
        appUser.userId( userDTO.getUserId() );
        appUser.username( userDTO.getUsername() );

        return appUser.build();
    }
}
