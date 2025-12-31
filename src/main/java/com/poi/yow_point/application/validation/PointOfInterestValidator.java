package com.poi.yow_point.application.validation;

import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PointOfInterestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PointOfInterestDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PointOfInterestDTO dto = (PointOfInterestDTO) target;

        // Validation du nom
        if (dto.getPoiName() == null || dto.getPoiName().trim().isEmpty()) {
            errors.rejectValue("poiName", "poi.name.required", "POI name is required");
        } else if (dto.getPoiName().length() > 100) {
            errors.rejectValue("poiName", "poi.name.length", "POI name must be less than 100 characters");
        }

        // Validation de l'organisation
        if (dto.getOrganizationId() == null) {
            errors.rejectValue("organizationId", "organization.id.required", "Organization ID is required");
        }

        // Validation des coordonnées géographiques
        if (dto.getLatitude() != null) {
            if (dto.getLatitude().compareTo(Double.valueOf(-90)) < 0 ||
                    dto.getLatitude().compareTo(Double.valueOf(90)) > 0) {
                errors.rejectValue("latitude", "invalid.latitude", "Latitude must be between -90 and 90");
            }
        }

        if (dto.getLongitude() != null) {
            if (dto.getLongitude().compareTo(Double.valueOf(-180)) < 0 ||
                    dto.getLongitude().compareTo(Double.valueOf(180)) > 0) {
                errors.rejectValue("longitude", "invalid.longitude", "Longitude must be between -180 and 180");
            }
        }

        // Validation du score de popularité
        if (dto.getPopularityScore() != null &&
                (dto.getPopularityScore() < 0 || dto.getPopularityScore() > 5)) {
            errors.rejectValue("popularityScore", "invalid.popularity", "Popularity score must be between 0 and 5");
        }

        // Validation des URLs
        if (dto.getWebsiteUrl() != null && dto.getWebsiteUrl().length() > 255) {
            errors.rejectValue("websiteUrl", "url.length", "Website URL must be less than 255 characters");
        }

        if (dto.getPoiImagesUrls() != null) {
            for (String url : dto.getPoiImagesUrls()) {
                if (url.length() > 255) {
                    errors.rejectValue("poiImagesUrls", "image.url.length",
                            "Image URL must be less than 255 characters");
                    break;
                }
            }
        }
    }
}