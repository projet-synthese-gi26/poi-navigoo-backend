package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.presentation.dto.user.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(AppUser user);

    AppUser toEntity(UserDTO userDTO);
}
