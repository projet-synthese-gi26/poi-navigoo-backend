package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.presentation.dto.user.UserDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-07T19:56:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(AppUser user) {
        if ( user == null ) {
            return null;
        }

        UserDTO.UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.userId( user.getUserId() );
        userDTO.organizationId( user.getOrganizationId() );
        userDTO.username( user.getUsername() );
        userDTO.email( user.getEmail() );
        userDTO.phone( user.getPhone() );
        userDTO.role( user.getRole() );
        userDTO.isActive( user.getIsActive() );
        userDTO.emailVerified( user.getEmailVerified() );
        userDTO.createdAt( user.getCreatedAt() );
        userDTO.lastLoginAt( user.getLastLoginAt() );

        return userDTO.build();
    }

    @Override
    public AppUser toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.userId( userDTO.getUserId() );
        appUser.organizationId( userDTO.getOrganizationId() );
        appUser.username( userDTO.getUsername() );
        appUser.email( userDTO.getEmail() );
        appUser.phone( userDTO.getPhone() );
        appUser.role( userDTO.getRole() );
        appUser.isActive( userDTO.getIsActive() );
        appUser.createdAt( userDTO.getCreatedAt() );
        appUser.emailVerified( userDTO.getEmailVerified() );
        appUser.lastLoginAt( userDTO.getLastLoginAt() );

        return appUser.build();
    }
}
