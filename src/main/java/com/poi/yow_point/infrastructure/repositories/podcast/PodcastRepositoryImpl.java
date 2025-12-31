package com.poi.yow_point.infrastructure.repositories.podcast;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.Podcast;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PodcastRepositoryImpl implements PodcastRepositoryCustom {

    private final R2dbcEntityTemplate template;

    public PodcastRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<Podcast> findByUserIdAndIsActiveTrue(UUID userId) {
        Query query = Query.query(
                Criteria.where("user_id").is(userId)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Podcast.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Podcast> findByPoiIdAndIsActiveTrue(UUID poiId) {
        Query query = Query.query(
                Criteria.where("poi_id").is(poiId)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Podcast.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Podcast> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title) {
        Query query = Query.query(
                Criteria.where("title").like("%" + title + "%").ignoreCase(true)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Podcast.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Podcast> findAllActivePodcasts() {
        Query query = Query.query(
                Criteria.where("is_active").is(true)).sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Podcast.class)
                .matching(query)
                .all();
    }

    @Override
    public Mono<Podcast> findByIdAndIsActiveTrue(UUID podcastId) {
        Query query = Query.query(
                Criteria.where("podcast_id").is(podcastId)
                        .and("is_active").is(true));

        return template.select(Podcast.class)
                .matching(query)
                .one();
    }

    @Override
    public Mono<Long> countByUserIdAndIsActiveTrue(UUID userId) {
        Query query = Query.query(
                Criteria.where("user_id").is(userId)
                        .and("is_active").is(true));

        return template.select(Podcast.class)
                .matching(query)
                .count();
    }

    @Override
    public Mono<Long> countByPoiIdAndIsActiveTrue(UUID poiId) {
        Query query = Query.query(
                Criteria.where("poi_id").is(poiId)
                        .and("is_active").is(true));

        return template.select(Podcast.class)
                .matching(query)
                .count();
    }

    @Override
    public Flux<Podcast> findByDurationRange(Integer minDuration, Integer maxDuration) {
        Query query = Query.query(
                Criteria.where("duration_seconds").between(minDuration, maxDuration)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Podcast.class)
                .matching(query)
                .all();
    }
}