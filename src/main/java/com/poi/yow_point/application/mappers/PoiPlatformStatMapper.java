package com.poi.yow_point.application.mappers;

import org.mapstruct.*;

import com.poi.yow_point.infrastructure.entities.PoiPlatformStat;
import com.poi.yow_point.presentation.dto.PoiPlatformStatDTO;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED)
public interface PoiPlatformStatMapper {

    // Mapping pour l'entité réactive (utilise directement les UUID)
    @Mapping(source = "orgId", target = "orgId")
    @Mapping(source = "poiId", target = "poiId")
    PoiPlatformStatDTO toDTO(PoiPlatformStat poiPlatformStat);

    // Mapping inverse
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "pointOfInterest", ignore = true)
    PoiPlatformStat toEntity(PoiPlatformStatDTO poiPlatformStatDTO);

    // Méthodes utilitaires pour le mapping avec les objets relationnels
    @Mapping(source = "organization.organizationId", target = "orgId")
    @Mapping(source = "pointOfInterest.poiId", target = "poiId")
    PoiPlatformStatDTO toDTOWithRelations(PoiPlatformStat poiPlatformStat);
}