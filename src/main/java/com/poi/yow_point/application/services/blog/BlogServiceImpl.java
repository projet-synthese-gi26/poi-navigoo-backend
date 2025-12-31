package com.poi.yow_point.application.services.blog;

import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.poi.yow_point.application.mappers.BlogMapper;
import com.poi.yow_point.application.validation.BlogValidator;
import com.poi.yow_point.infrastructure.repositories.blog.BlogRepository;
import com.poi.yow_point.presentation.dto.blogDto.BlogDTO;
import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogMapper blogMapper;
    private final R2dbcEntityTemplate entityTemplate;
    private final BlogValidator blogValidator;

    public BlogServiceImpl(BlogRepository blogRepository, BlogMapper blogMapper, R2dbcEntityTemplate entityTemplate,
            BlogValidator blogValidator) {
        this.blogRepository = blogRepository;
        this.blogMapper = blogMapper;
        this.blogValidator = blogValidator;
        this.entityTemplate = entityTemplate;
    }

    @Override
    public Mono<BlogDTO> createBlog(CreateBlogRequest blogCreateDto) {
        return blogValidator.validateCreateDto(blogCreateDto)
                .then(Mono.fromCallable(() -> blogMapper.toEntity(blogCreateDto)))
                .flatMap(blog -> entityTemplate.insert(blog))
                .map(blogMapper::toDto);
    }

    @Override
    public Mono<BlogDTO> updateBlog(UUID blogId, UpdateBlogRequest blogUpdateDto) {
        return blogValidator.validateUpdateDto(blogUpdateDto)
                .then(blogRepository.findById(blogId))
                .switchIfEmpty(Mono.error(new RuntimeException("Blog not found with id: " + blogId)))
                .doOnNext(blog -> blogMapper.updateEntityFromDto(blogUpdateDto, blog))
                .flatMap(blogRepository::save)
                .map(blogMapper::toDto);
    }

    @Override
    public Mono<BlogDTO> getBlogById(UUID blogId) {
        return blogRepository.findByIdAndIsActiveTrue(blogId)
                .switchIfEmpty(Mono.error(new RuntimeException("Blog not found with id: " + blogId)))
                .map(blogMapper::toDto);
    }

    @Override
    public Flux<BlogDTO> getAllBlogs() {
        return blogRepository.findAllActiveBlogs()
                .map(blogMapper::toDto);
    }

    @Override
    public Flux<BlogDTO> getBlogsByUserId(UUID userId) {
        return blogRepository.findByUserIdAndIsActiveTrue(userId)
                .map(blogMapper::toDto);
    }

    @Override
    public Flux<BlogDTO> getBlogsByPoiId(UUID poiId) {
        return blogRepository.findByPoiIdAndIsActiveTrue(poiId)
                .map(blogMapper::toDto);
    }

    @Override
    public Flux<BlogDTO> searchBlogsByTitle(String title) {
        return blogRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue("%" + title + "%")
                .map(blogMapper::toDto);
    }

    @Override
    public Mono<Void> deleteBlog(UUID blogId) {
        return blogRepository.findById(blogId)
                .switchIfEmpty(Mono.error(new RuntimeException("Blog not found with id: " + blogId)))
                .doOnNext(blog -> blog.setIsActive(false))
                .flatMap(blogRepository::save)
                .then();
    }

    @Override
    public Mono<Long> countBlogsByUserId(UUID userId) {
        return blogRepository.countByUserIdAndIsActiveTrue(userId);
    }

    @Override
    public Mono<Long> countBlogsByPoiId(UUID poiId) {
        return blogRepository.countByPoiIdAndIsActiveTrue(poiId);
    }
}