package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Blog;
import com.poi.yow_point.presentation.dto.blogDto.BlogDTO;
import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T17:19:59+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BlogMapperImpl implements BlogMapper {

    @Override
    public BlogDTO toDto(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogDTO.BlogDTOBuilder blogDTO = BlogDTO.builder();

        blogDTO.blogId( blog.getBlogId() );
        blogDTO.content( blog.getContent() );
        blogDTO.coverImageUrl( blog.getCoverImageUrl() );
        blogDTO.createdAt( blog.getCreatedAt() );
        blogDTO.description( blog.getDescription() );
        blogDTO.isActive( blog.getIsActive() );
        blogDTO.poiId( blog.getPoiId() );
        blogDTO.title( blog.getTitle() );
        blogDTO.updatedAt( blog.getUpdatedAt() );
        blogDTO.userId( blog.getUserId() );

        return blogDTO.build();
    }

    @Override
    public Blog toEntity(BlogDTO blogDto) {
        if ( blogDto == null ) {
            return null;
        }

        Blog.BlogBuilder blog = Blog.builder();

        blog.blogId( blogDto.getBlogId() );
        blog.content( blogDto.getContent() );
        blog.coverImageUrl( blogDto.getCoverImageUrl() );
        blog.createdAt( blogDto.getCreatedAt() );
        blog.description( blogDto.getDescription() );
        blog.isActive( blogDto.getIsActive() );
        blog.poiId( blogDto.getPoiId() );
        blog.title( blogDto.getTitle() );
        blog.updatedAt( blogDto.getUpdatedAt() );
        blog.userId( blogDto.getUserId() );

        return blog.build();
    }

    @Override
    public Blog toEntity(CreateBlogRequest blogCreateDto) {
        if ( blogCreateDto == null ) {
            return null;
        }

        Blog.BlogBuilder blog = Blog.builder();

        blog.content( blogCreateDto.getContent() );
        blog.coverImageUrl( blogCreateDto.getCoverImageUrl() );
        blog.description( blogCreateDto.getDescription() );
        blog.poiId( blogCreateDto.getPoiId() );
        blog.title( blogCreateDto.getTitle() );
        blog.userId( blogCreateDto.getUserId() );

        blog.blogId( java.util.UUID.randomUUID() );
        blog.isActive( true );
        blog.createdAt( java.time.LocalDateTime.now() );
        blog.updatedAt( java.time.LocalDateTime.now() );

        return blog.build();
    }

    @Override
    public void updateEntityFromDto(UpdateBlogRequest blogUpdateDto, Blog blog) {
        if ( blogUpdateDto == null ) {
            return;
        }

        if ( blogUpdateDto.getContent() != null ) {
            blog.setContent( blogUpdateDto.getContent() );
        }
        if ( blogUpdateDto.getCoverImageUrl() != null ) {
            blog.setCoverImageUrl( blogUpdateDto.getCoverImageUrl() );
        }
        if ( blogUpdateDto.getDescription() != null ) {
            blog.setDescription( blogUpdateDto.getDescription() );
        }
        if ( blogUpdateDto.getIsActive() != null ) {
            blog.setIsActive( blogUpdateDto.getIsActive() );
        }
        if ( blogUpdateDto.getTitle() != null ) {
            blog.setTitle( blogUpdateDto.getTitle() );
        }

        blog.setUpdatedAt( java.time.LocalDateTime.now() );
    }
}
