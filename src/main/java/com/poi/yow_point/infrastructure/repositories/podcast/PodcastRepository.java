package com.poi.yow_point.infrastructure.repositories.podcast;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.Podcast;

@Repository
public interface PodcastRepository extends R2dbcRepository<Podcast, UUID>, PodcastRepositoryCustom {

}
