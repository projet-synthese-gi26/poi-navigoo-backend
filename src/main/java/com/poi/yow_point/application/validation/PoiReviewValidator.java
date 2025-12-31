package com.poi.yow_point.application.validation;

import com.poi.yow_point.presentation.dto.PoiReviewDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PoiReviewValidator {

    public Mono<Void> validateCreateReview(PoiReviewDTO reviewDTO) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (reviewDTO.getPoiId() == null) {
                errors.add("POI ID is required");
            }

            if (reviewDTO.getUserId() == null) {
                errors.add("User ID is required");
            }

            if (reviewDTO.getOrganizationId() == null) {
                errors.add("Organization ID is required");
            }

            if (reviewDTO.getRating() == null) {
                errors.add("Rating is required");
            } else if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
                errors.add("Rating must be between 1 and 5");
            }

            if (reviewDTO.getReviewText() == null || reviewDTO.getReviewText().trim().isEmpty()) {
                errors.add("Review text is required");
            } else if (reviewDTO.getReviewText().length() > 1000) {
                errors.add("Review text cannot exceed 1000 characters");
            }

            if (reviewDTO.getPlatformType() == null || reviewDTO.getPlatformType().trim().isEmpty()) {
                errors.add("Platform type is required");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
            }

            return null;
        }).then();
    }

    public Mono<Void> validateUpdateReview(PoiReviewDTO reviewDTO) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (reviewDTO.getRating() != null && (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5)) {
                errors.add("Rating must be between 1 and 5");
            }

            if (reviewDTO.getReviewText() != null && reviewDTO.getReviewText().length() > 1000) {
                errors.add("Review text cannot exceed 1000 characters");
            }

            if (reviewDTO.getLikes() != null && reviewDTO.getLikes() < 0) {
                errors.add("Likes cannot be negative");
            }

            if (reviewDTO.getDislikes() != null && reviewDTO.getDislikes() < 0) {
                errors.add("Dislikes cannot be negative");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
            }

            return null;
        }).then();
    }

    public Mono<Void> validateUUID(UUID id, String fieldName) {
        return Mono.fromCallable(() -> {
            if (id == null) {
                throw new IllegalArgumentException(fieldName + " cannot be null");
            }
            return null;
        }).then();
    }
}