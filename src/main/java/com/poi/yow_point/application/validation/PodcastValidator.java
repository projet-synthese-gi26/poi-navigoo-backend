package com.poi.yow_point.application.validation;

import org.springframework.stereotype.Component;

import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;

import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Component
public class PodcastValidator {

    public Mono<Void> validateCreateDto(PodcastCreateRequest podcastCreateDto) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (podcastCreateDto.getUserId() == null) {
                errors.add("User ID is required");
            }

            if (podcastCreateDto.getPoiId() == null) {
                errors.add("POI ID is required");
            }

            if (podcastCreateDto.getTitle() == null || podcastCreateDto.getTitle().trim().isEmpty()) {
                errors.add("Title is required");
            } else if (podcastCreateDto.getTitle().length() > 255) {
                errors.add("Title must not exceed 255 characters");
            }

            if (podcastCreateDto.getDescription() != null && podcastCreateDto.getDescription().length() > 1000) {
                errors.add("Description must not exceed 1000 characters");
            }

            if (podcastCreateDto.getCoverImageUrl() != null && !isValidUrl(podcastCreateDto.getCoverImageUrl())) {
                errors.add("Cover image URL is not valid");
            }

            if (podcastCreateDto.getAudioFileUrl() != null && !isValidUrl(podcastCreateDto.getAudioFileUrl())) {
                errors.add("Audio file URL is not valid");
            }

            if (podcastCreateDto.getDurationSeconds() != null && podcastCreateDto.getDurationSeconds() < 0) {
                errors.add("Duration seconds must be positive");
            }

            if (!errors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
            }

            return null;
        }).then();
    }

    public Mono<Void> validateUpdateDto(UpdatePodcastRequest podcastUpdateDto) {
        return Mono.fromCallable(() -> {
            List<String> errors = new ArrayList<>();

            if (podcastUpdateDto.getTitle() != null) {
                if (podcastUpdateDto.getTitle().trim().isEmpty()) {
                    errors.add("Title cannot be empty");
                } else if (podcastUpdateDto.getTitle().length() > 255) {
                    errors.add("Title must not exceed 255 characters");
                }
            }

            if (podcastUpdateDto.getDescription() != null && podcastUpdateDto.getDescription().length() > 1000) {
                errors.add("Description must not exceed 1000 characters");
            }

            if (podcastUpdateDto.getCoverImageUrl() != null && !isValidUrl(podcastUpdateDto.getCoverImageUrl())) {
                errors.add("Cover image URL is not valid");
            }

            if (podcastUpdateDto.getAudioFileUrl() != null && !isValidUrl(podcastUpdateDto.getAudioFileUrl())) {
                errors.add("Audio file URL is not valid");
            }

            if (podcastUpdateDto.getDurationSeconds() != null && podcastUpdateDto.getDurationSeconds() < 0) {
                errors.add("Duration seconds must be positive");
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
