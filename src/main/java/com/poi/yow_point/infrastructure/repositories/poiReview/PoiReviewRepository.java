package com.poi.yow_point.infrastructure.repositories.poiReview;

import com.poi.yow_point.infrastructure.entities.PoiReview;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface PoiReviewRepository extends R2dbcRepository<PoiReview, UUID>, PoiReviewRepositoryCustom {
    // This interface extends R2dbcRepository for basic CRUD operations
    // and PoiReviewRepositoryCustom for custom query methods.

}