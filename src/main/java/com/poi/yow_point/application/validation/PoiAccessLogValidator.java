package com.poi.yow_point.application.validation;

import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class PoiAccessLogValidator {

    public Mono<PoiAccessLogDTO> validate(PoiAccessLogDTO dto) {
        return Mono.fromCallable(() -> {
            if (dto == null) {
                throw new IllegalArgumentException("Access log DTO cannot be null");
            }

            if (dto.getPoiId() == null) {
                throw new IllegalArgumentException("POI ID cannot be null");
            }

            if (dto.getOrganizationId() == null) {
                throw new IllegalArgumentException("Organization ID cannot be null");
            }

            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            if (dto.getAccessType() == null || dto.getAccessType().isBlank()) {
                throw new IllegalArgumentException("Access type cannot be null or empty");
            }

            if (dto.getPlatformType() == null || dto.getPlatformType().isBlank()) {
                throw new IllegalArgumentException("Platform type cannot be null or empty");
            }

            return dto;
        });
    }

    public Mono<UUID> validateId(UUID id) {
        return Mono.fromCallable(() -> {
            if (id == null) {
                throw new IllegalArgumentException("ID cannot be null");
            }
            return id;
        });
    }
}