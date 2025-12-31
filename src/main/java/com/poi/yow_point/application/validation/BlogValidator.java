package com.poi.yow_point.application.validation;

import org.springframework.stereotype.Component;

import com.poi.yow_point.presentation.dto.blogDto.CreateBlogRequest;
import com.poi.yow_point.presentation.dto.blogDto.UpdateBlogRequest;

import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Component
public class BlogValidator {

    public Mono<Void> validateCreateDto(CreateBlogRequest blogCreateDto) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (blogCreateDto.getUserId() == null) {
                errors.add("User ID is required");
            }

            if (blogCreateDto.getPoiId() == null) {
                errors.add("POI ID is required");
            }

            if (blogCreateDto.getTitle() == null || blogCreateDto.getTitle().trim().isEmpty()) {
                errors.add("Title is required");
            } else if (blogCreateDto.getTitle().length() > 255) {
                errors.add("Title must not exceed 255 characters");
            }

            if (blogCreateDto.getDescription() != null && blogCreateDto.getDescription().length() > 1000) {
                errors.add("Description must not exceed 1000 characters");
            }

            if (blogCreateDto.getCoverImageUrl() != null && !isValidUrl(blogCreateDto.getCoverImageUrl())) {
                errors.add("Cover image URL is not valid");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
            }

            return null;
        }).then();
    }

    public Mono<Void> validateUpdateDto(UpdateBlogRequest blogUpdateDto) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (blogUpdateDto.getTitle() != null) {
                if (blogUpdateDto.getTitle().trim().isEmpty()) {
                    errors.add("Title cannot be empty");
                } else if (blogUpdateDto.getTitle().length() > 255) {
                    errors.add("Title must not exceed 255 characters");
                }
            }

            if (blogUpdateDto.getDescription() != null && blogUpdateDto.getDescription().length() > 1000) {
                errors.add("Description must not exceed 1000 characters");
            }

            if (blogUpdateDto.getCoverImageUrl() != null && !isValidUrl(blogUpdateDto.getCoverImageUrl())) {
                errors.add("Cover image URL is not valid");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
            }

            return null;
        }).then();
    }

    private boolean isValidUrl(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}