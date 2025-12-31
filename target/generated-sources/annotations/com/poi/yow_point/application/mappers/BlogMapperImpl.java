package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Blog;
import com.poi.yow_point.presentation.dto.blogDto.BlogDTO;
import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:02:09+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
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
        blogDTO.userId( blog.getUserId() );
        blogDTO.poiId( blog.getPoiId() );
        blogDTO.title( blog.getTitle() );
        blogDTO.description( blog.getDescription() );
        blogDTO.coverImageUrl( blog.getCoverImageUrl() );
        blogDTO.content( blog.getContent() );
        blogDTO.isActive( blog.getIsActive() );
        blogDTO.createdAt( blog.getCreatedAt() );
        blogDTO.updatedAt( blog.getUpdatedAt() );

        return blogDTO.build();
    }

    @Override
    public Blog toEntity(BlogDTO blogDto) {
        if ( blogDto == null ) {
            return null;
        }

        Blog.BlogBuilder blog = Blog.builder();

        blog.blogId( blogDto.getBlogId() );
        blog.userId( blogDto.getUserId() );
        blog.poiId( blogDto.getPoiId() );
        blog.title( blogDto.getTitle() );
        blog.description( blogDto.getDescription() );
        blog.coverImageUrl( blogDto.getCoverImageUrl() );
        blog.content( blogDto.getContent() );
        blog.isActive( blogDto.getIsActive() );
        blog.createdAt( blogDto.getCreatedAt() );
        blog.updatedAt( blogDto.getUpdatedAt() );

        return blog.build();
    }

    @Override
    public Blog toEntity(CreateBlogRequest blogCreateDto) {
        if ( blogCreateDto == null ) {
            return null;
        }

        Blog.BlogBuilder blog = Blog.builder();

        blog.userId( blogCreateDto.getUserId() );
        blog.poiId( blogCreateDto.getPoiId() );
        blog.title( blogCreateDto.getTitle() );
        blog.description( blogCreateDto.getDescription() );
        blog.coverImageUrl( blogCreateDto.getCoverImageUrl() );
        blog.content( blogCreateDto.getContent() );

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

        if ( blogUpdateDto.getTitle() != null ) {
            blog.setTitle( blogUpdateDto.getTitle() );
        }
        if ( blogUpdateDto.getDescription() != null ) {
            blog.setDescription( blogUpdateDto.getDescription() );
        }
        if ( blogUpdateDto.getCoverImageUrl() != null ) {
            blog.setCoverImageUrl( blogUpdateDto.getCoverImageUrl() );
        }
        if ( blogUpdateDto.getContent() != null ) {
            blog.setContent( blogUpdateDto.getContent() );
        }
        if ( blogUpdateDto.getIsActive() != null ) {
            blog.setIsActive( blogUpdateDto.getIsActive() );
        }

        blog.setUpdatedAt( java.time.LocalDateTime.now() );
    }
}
