package com.poi.yow_point.infrastructure.repositories.blog;

import java.util.UUID;

import com.poi.yow_point.infrastructure.entities.Blog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlogRepositoryCustom {

    Flux<Blog> findByUserIdAndIsActiveTrue(UUID userId);

    Flux<Blog> findByPoiIdAndIsActiveTrue(UUID poiId);

    Flux<Blog> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    Flux<Blog> findAllActiveBlogs();

    Mono<Blog> findByIdAndIsActiveTrue(UUID blogId);

    Mono<Long> countByUserIdAndIsActiveTrue(UUID userId);

    Mono<Long> countByPoiIdAndIsActiveTrue(UUID poiId);
}