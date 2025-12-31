package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.AppUser;
import com.poi.yow_point.presentation.dto.AppUserDTO;

import org.mapstruct.*;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public interface AppUserMapper {

    // Mapping pour la lecture : exclut le passwordHash
    @Mapping(target = "password", ignore = true)
    AppUserDTO toDTO(AppUser appUser);

    // Mapping pour la création : ignore les champs auto-générés et le password
    // (sera géré séparément)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    AppUser toEntity(AppUserDTO appUserDTO);

    // Mapping pour la mise à jour : ignore les champs non modifiables et le
    // password
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(AppUserDTO dto, @MappingTarget AppUser entity);

    // Mapping spécifique pour la création avec mot de passe
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "passwordHash", source = "passwordHash")
    AppUser toEntityWithPassword(AppUserDTO appUserDTO, String passwordHash);
}