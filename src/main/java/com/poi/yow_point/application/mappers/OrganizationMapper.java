package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Organization;
import com.poi.yow_point.presentation.dto.OrganizationDTO;

import org.mapstruct.*;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public interface OrganizationMapper {

    OrganizationDTO toDTO(Organization organization);

    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Organization toEntity(OrganizationDTO organizationDTO);

    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(OrganizationDTO dto, @MappingTarget Organization entity);

    // Note: Dans un contexte réactif avec R2DBC, les mappings sont simplifiés car
    // les relations OneToMany ne sont plus gérées directement dans l'entité.
    // Les relations sont généralement gérées par des services séparés qui
    // retournent
    // des Mono<T> ou Flux<T> pour les opérations asynchrones.
}