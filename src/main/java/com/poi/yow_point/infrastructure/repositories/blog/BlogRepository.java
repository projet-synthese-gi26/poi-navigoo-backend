package com.poi.yow_point.infrastructure.repositories.blog;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.Blog;

@Repository
public interface BlogRepository extends R2dbcRepository<Blog, UUID>, BlogRepositoryCustom {

}