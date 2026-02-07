package com.poi.yow_point.infrastructure.repositories.poiReview;

import com.poi.yow_point.infrastructure.entities.Review;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
//import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface ReviewRepository extends R2dbcRepository<Review, UUID>, ReviewRepositoryCustom {
    // This interface extends R2dbcRepository for basic CRUD operations
    // and ReviewRepositoryCustom for custom query methods.

    Flux<Review> findByBlogId(UUID blogId);
    Flux<Review> findByPodcastId(UUID podcastId);

}