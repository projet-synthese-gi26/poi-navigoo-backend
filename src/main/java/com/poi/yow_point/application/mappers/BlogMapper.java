package com.poi.yow_point.application.mappers;

import org.mapstruct.*;

import com.poi.yow_point.infrastructure.entities.Blog;
import com.poi.yow_point.presentation.dto.blogDto.BlogDTO;
import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {

    BlogDTO toDto(Blog blog);

    Blog toEntity(BlogDTO blogDto);

    @Mapping(target = "blogId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Blog toEntity(CreateBlogRequest blogCreateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "blogId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "poiId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UpdateBlogRequest blogUpdateDto, @MappingTarget Blog blog);
}