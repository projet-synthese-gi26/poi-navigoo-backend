package com.poi.yow_point.application.mappers;

import org.mapstruct.*;

import com.poi.yow_point.infrastructure.entities.Podcast;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastDTO;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PodcastMapper {

    PodcastDTO toDto(Podcast podcast);

    Podcast toEntity(PodcastDTO podcastDto);

    @Mapping(target = "podcastId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Podcast toEntity(PodcastCreateRequest podcastCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "podcastId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "poiId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UpdatePodcastRequest podcastUpdateDto, @MappingTarget Podcast podcast);
}
