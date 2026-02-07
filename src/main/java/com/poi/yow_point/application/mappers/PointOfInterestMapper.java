package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import org.mapstruct.*;

import java.time.Instant;

@Mapper(componentModel = "spring", uses = {MapperUtils.class})
public interface PointOfInterestMapper {

    @Mapping(source = "locationGeog", target = "latitude", qualifiedByName = "pointToLatitude")
    @Mapping(source = "locationGeog", target = "longitude", qualifiedByName = "pointToLongitude")
    @Mapping(source = "poiImagesUrls", target = "poiImagesUrls", qualifiedByName = "stringToList")
    @Mapping(source = "poiAmenities", target = "poiAmenities", qualifiedByName = "stringToList")
    @Mapping(source = "poiKeywords", target = "poiKeywords", qualifiedByName = "stringToList")
    @Mapping(source = "poiTypeTags", target = "poiTypeTags", qualifiedByName = "stringToList")
    @Mapping(source = "operationTimePlan", target = "operationTimePlan", qualifiedByName = "jsonToMap")
    @Mapping(source = "poiContacts", target = "poiContacts", qualifiedByName = "jsonToMap")
    PointOfInterestDTO toDto(PointOfInterest entity);




    @Mapping(target = "poiId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approuvedByUserId", ignore = true)
    @Mapping(target = "deactivatedByUserId", ignore = true)
    @Mapping(target = "deactivationReason", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "updatedByUserId", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "locationGeog", expression = "java(mapperUtils.coordinatesToPoint(dto.getLatitude(), dto.getLongitude()))")
    @Mapping(source = "poiImagesUrls", target = "poiImagesUrls", qualifiedByName = "listToString")
    @Mapping(source = "poiAmenities", target = "poiAmenities", qualifiedByName = "listToString")
    @Mapping(source = "poiKeywords", target = "poiKeywords", qualifiedByName = "listToString")
    @Mapping(source = "poiTypeTags", target = "poiTypeTags", qualifiedByName = "listToString")
    @Mapping(source = "operationTimePlan", target = "operationTimePlan", qualifiedByName = "mapToJson")
    @Mapping(source = "poiContacts", target = "poiContacts", qualifiedByName = "mapToJson")
    PointOfInterest toEntity(com.poi.yow_point.presentation.dto.CreatePoiDTO dto, @Context MapperUtils mapperUtils);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "poiId", ignore = true)
    @Mapping(target = "createdByUserId", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approuvedByUserId", ignore = true)
    @Mapping(target = "locationGeog", expression = "java(mapperUtils.coordinatesToPoint(dto.getLatitude(), dto.getLongitude()))")
    @Mapping(source = "poiImagesUrls", target = "poiImagesUrls", qualifiedByName = "listToString")
    @Mapping(source = "poiAmenities", target = "poiAmenities", qualifiedByName = "listToString")
    @Mapping(source = "poiKeywords", target = "poiKeywords", qualifiedByName = "listToString")
    @Mapping(source = "poiTypeTags", target = "poiTypeTags", qualifiedByName = "listToString")
    @Mapping(source = "operationTimePlan", target = "operationTimePlan", qualifiedByName = "mapToJson")
    @Mapping(source = "poiContacts", target = "poiContacts", qualifiedByName = "mapToJson")
    void updateEntityFromDto(@MappingTarget PointOfInterest entity, com.poi.yow_point.presentation.dto.UpdatePoiDTO dto, @Context MapperUtils mapperUtils);

    @AfterMapping
    default void setUpdateTimestamp(@MappingTarget PointOfInterest entity) {
        entity.setUpdatedAt(Instant.now());
    }
}
