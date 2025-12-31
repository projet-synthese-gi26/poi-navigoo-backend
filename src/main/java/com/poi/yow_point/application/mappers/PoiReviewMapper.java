package com.poi.yow_point.application.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.poi.yow_point.infrastructure.entities.PoiReview;
import com.poi.yow_point.presentation.dto.PoiReviewDTO;

@Mapper(componentModel = "spring")
public interface PoiReviewMapper {
    PoiReviewMapper INSTANCE = Mappers.getMapper(PoiReviewMapper.class);

    // Mapping direct car l'entité contient maintenant directement les IDs
    PoiReviewDTO toDTO(PoiReview poiReview);

    // Mapping direct également
    @Mapping(target = "reviewId", ignore = true)
    PoiReview toEntity(PoiReviewDTO poiReviewDTO);
}