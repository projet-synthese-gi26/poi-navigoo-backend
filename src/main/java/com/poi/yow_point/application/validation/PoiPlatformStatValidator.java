package com.poi.yow_point.application.validation;

import com.poi.yow_point.presentation.dto.PoiPlatformStatDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class PoiPlatformStatValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PoiPlatformStatDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PoiPlatformStatDTO dto = (PoiPlatformStatDTO) target;

        if (dto.getStatDate() != null && dto.getStatDate().isAfter(LocalDate.now())) {
            errors.rejectValue("statDate", "future.date", "La date ne peut pas être dans le futur");
        }

        if (dto.getPlatformType() == null || dto.getPlatformType().trim().isEmpty()) {
            errors.rejectValue("platformType", "empty.platform", "Le type de plateforme est requis");
        }

        if (dto.getViews() != null && dto.getViews() < 0) {
            errors.rejectValue("views", "negative.views", "Les vues ne peuvent pas être négatives");
        }

        if (dto.getLikes() != null && dto.getLikes() < 0) {
            errors.rejectValue("likes", "negative.likes", "Les likes ne peuvent pas être négatifs");
        }

        if (dto.getDislikes() != null && dto.getDislikes() < 0) {
            errors.rejectValue("dislikes", "negative.dislikes", "Les dislikes ne peuvent pas être négatifs");
        }

        if (dto.getReviews() != null && dto.getReviews() < 0) {
            errors.rejectValue("reviews", "negative.reviews", "Les reviews ne peuvent pas être négatives");
        }
    }
}