package com.poi.yow_point.infrastructure.repositories.blog;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.Blog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BlogRepositoryImpl implements BlogRepositoryCustom {

    private final R2dbcEntityTemplate template;

    public BlogRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<Blog> findByUserIdAndIsActiveTrue(UUID userId) {
        Query query = Query.query(
                Criteria.where("user_id").is(userId)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Blog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Blog> findByPoiIdAndIsActiveTrue(UUID poiId) {
        Query query = Query.query(
                Criteria.where("poi_id").is(poiId)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Blog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Blog> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title) {
        Query query = Query.query(
                Criteria.where("title").like("%" + title + "%").ignoreCase(true)
                        .and("is_active").is(true))
                .sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Blog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Blog> findAllActiveBlogs() {
        Query query = Query.query(
                Criteria.where("is_active").is(true)).sort(Sort.by(Sort.Direction.DESC, "created_at"));

        return template.select(Blog.class)
                .matching(query)
                .all();
    }

    @Override
    public Mono<Blog> findByIdAndIsActiveTrue(UUID blogId) {
        Query query = Query.query(
                Criteria.where("blog_id").is(blogId)
                        .and("is_active").is(true));

        return template.select(Blog.class)
                .matching(query)
                .one();
    }

    @Override
    public Mono<Long> countByUserIdAndIsActiveTrue(UUID userId) {
        Query query = Query.query(
                Criteria.where("user_id").is(userId)
                        .and("is_active").is(true));

        return template.select(Blog.class)
                .matching(query)
                .count();
    }

    @Override
    public Mono<Long> countByPoiIdAndIsActiveTrue(UUID poiId) {
        Query query = Query.query(
                Criteria.where("poi_id").is(poiId)
                        .and("is_active").is(true));

        return template.select(Blog.class)
                .matching(query)
                .count();
    }
}