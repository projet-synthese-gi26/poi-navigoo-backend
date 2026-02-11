package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PoiReview;
import com.poi.yow_point.presentation.dto.PoiReviewDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-11T12:44:15+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class PoiReviewMapperImpl implements PoiReviewMapper {

    @Override
    public PoiReviewDTO toDTO(PoiReview poiReview) {
        if ( poiReview == null ) {
            return null;
        }

        PoiReviewDTO.PoiReviewDTOBuilder poiReviewDTO = PoiReviewDTO.builder();

        poiReviewDTO.reviewId( poiReview.getReviewId() );
        poiReviewDTO.poiId( poiReview.getPoiId() );
        poiReviewDTO.userId( poiReview.getUserId() );
        poiReviewDTO.organizationId( poiReview.getOrganizationId() );
        poiReviewDTO.platformType( poiReview.getPlatformType() );
        poiReviewDTO.rating( poiReview.getRating() );
        poiReviewDTO.reviewText( poiReview.getReviewText() );
        poiReviewDTO.createdAt( poiReview.getCreatedAt() );
        poiReviewDTO.likes( poiReview.getLikes() );
        poiReviewDTO.dislikes( poiReview.getDislikes() );

        return poiReviewDTO.build();
    }

    @Override
    public PoiReview toEntity(PoiReviewDTO poiReviewDTO) {
        if ( poiReviewDTO == null ) {
            return null;
        }

        PoiReview.PoiReviewBuilder poiReview = PoiReview.builder();

        poiReview.poiId( poiReviewDTO.getPoiId() );
        poiReview.userId( poiReviewDTO.getUserId() );
        poiReview.organizationId( poiReviewDTO.getOrganizationId() );
        poiReview.platformType( poiReviewDTO.getPlatformType() );
        poiReview.rating( poiReviewDTO.getRating() );
        poiReview.reviewText( poiReviewDTO.getReviewText() );
        poiReview.createdAt( poiReviewDTO.getCreatedAt() );
        poiReview.likes( poiReviewDTO.getLikes() );
        poiReview.dislikes( poiReviewDTO.getDislikes() );

        return poiReview.build();
    }
}
