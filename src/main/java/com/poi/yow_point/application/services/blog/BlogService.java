package com.poi.yow_point.application.services.blog;

import java.util.UUID;

import com.poi.yow_point.presentation.dto.blogDto.BlogDTO;
import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlogService {
    Mono<BlogDTO> createBlog(CreateBlogRequest blogCreateDto);

    Mono<BlogDTO> updateBlog(UUID blogId, UpdateBlogRequest blogUpdateDto);

    Mono<BlogDTO> getBlogById(UUID blogId);

    Flux<BlogDTO> getAllBlogs();

    Flux<BlogDTO> getBlogsByUserId(UUID userId);

    Flux<BlogDTO> getBlogsByPoiId(UUID poiId);

    Flux<BlogDTO> searchBlogsByTitle(String title);

    Mono<Void> deleteBlog(UUID blogId);

    Mono<Long> countBlogsByUserId(UUID userId);

    Mono<Long> countBlogsByPoiId(UUID poiId);
}
