package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PoiReview;
import com.poi.yow_point.presentation.dto.PoiReviewDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T21:00:14+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PoiReviewMapperImpl implements PoiReviewMapper {

    @Override
    public PoiReviewDTO toDTO(PoiReview poiReview) {
        if ( poiReview == null ) {
            return null;
        }

        PoiReviewDTO.PoiReviewDTOBuilder poiReviewDTO = PoiReviewDTO.builder();

        poiReviewDTO.createdAt( poiReview.getCreatedAt() );
        poiReviewDTO.dislikes( poiReview.getDislikes() );
        poiReviewDTO.likes( poiReview.getLikes() );
        poiReviewDTO.organizationId( poiReview.getOrganizationId() );
        poiReviewDTO.platformType( poiReview.getPlatformType() );
        poiReviewDTO.poiId( poiReview.getPoiId() );
        poiReviewDTO.rating( poiReview.getRating() );
        poiReviewDTO.reviewId( poiReview.getReviewId() );
        poiReviewDTO.reviewText( poiReview.getReviewText() );
        poiReviewDTO.userId( poiReview.getUserId() );

        return poiReviewDTO.build();
    }

    @Override
    public PoiReview toEntity(PoiReviewDTO poiReviewDTO) {
        if ( poiReviewDTO == null ) {
            return null;
        }

        PoiReview.PoiReviewBuilder poiReview = PoiReview.builder();

        poiReview.createdAt( poiReviewDTO.getCreatedAt() );
        poiReview.dislikes( poiReviewDTO.getDislikes() );
        poiReview.likes( poiReviewDTO.getLikes() );
        poiReview.organizationId( poiReviewDTO.getOrganizationId() );
        poiReview.platformType( poiReviewDTO.getPlatformType() );
        poiReview.poiId( poiReviewDTO.getPoiId() );
        poiReview.rating( poiReviewDTO.getRating() );
        poiReview.reviewText( poiReviewDTO.getReviewText() );
        poiReview.userId( poiReviewDTO.getUserId() );

        return poiReview.build();
    }
}
