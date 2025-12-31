package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PoiAccessLog;
import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { MapperUtils.class })
public interface PoiAccessLogMapper {

    /**
     * Convertit une entité PoiAccessLog en son DTO.
     * La conversion de `JsonNode` en `Map` est gérée automatiquement
     * car MapStruct trouve la méthode `jsonNodeToMap` dans `MapperUtils`.
     */
    PoiAccessLogDTO toDto(PoiAccessLog entity);

    /**
     * Convertit un DTO PoiAccessLog en son entité.
     * La conversion de `Map` en `JsonNode` est gérée automatiquement
     * car MapStruct trouve la méthode `mapToJsonNode` dans `MapperUtils`.
     */
    @Mapping(target = "metadata", ignore = true)
    PoiAccessLog toEntity(PoiAccessLogDTO dto);

}