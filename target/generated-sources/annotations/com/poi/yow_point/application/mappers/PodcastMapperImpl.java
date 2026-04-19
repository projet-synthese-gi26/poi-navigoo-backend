package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Podcast;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastDTO;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T21:00:14+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PodcastMapperImpl implements PodcastMapper {

    @Override
    public PodcastDTO toDto(Podcast podcast) {
        if ( podcast == null ) {
            return null;
        }

        PodcastDTO.PodcastDTOBuilder podcastDTO = PodcastDTO.builder();

        podcastDTO.audioFileUrl( podcast.getAudioFileUrl() );
        podcastDTO.coverImageUrl( podcast.getCoverImageUrl() );
        podcastDTO.createdAt( podcast.getCreatedAt() );
        podcastDTO.description( podcast.getDescription() );
        podcastDTO.durationSeconds( podcast.getDurationSeconds() );
        podcastDTO.isActive( podcast.getIsActive() );
        podcastDTO.podcastId( podcast.getPodcastId() );
        podcastDTO.poiId( podcast.getPoiId() );
        podcastDTO.title( podcast.getTitle() );
        podcastDTO.updatedAt( podcast.getUpdatedAt() );
        podcastDTO.userId( podcast.getUserId() );

        return podcastDTO.build();
    }

    @Override
    public Podcast toEntity(PodcastDTO podcastDto) {
        if ( podcastDto == null ) {
            return null;
        }

        Podcast.PodcastBuilder podcast = Podcast.builder();

        podcast.audioFileUrl( podcastDto.getAudioFileUrl() );
        podcast.coverImageUrl( podcastDto.getCoverImageUrl() );
        podcast.createdAt( podcastDto.getCreatedAt() );
        podcast.description( podcastDto.getDescription() );
        podcast.durationSeconds( podcastDto.getDurationSeconds() );
        podcast.isActive( podcastDto.getIsActive() );
        podcast.podcastId( podcastDto.getPodcastId() );
        podcast.poiId( podcastDto.getPoiId() );
        podcast.title( podcastDto.getTitle() );
        podcast.updatedAt( podcastDto.getUpdatedAt() );
        podcast.userId( podcastDto.getUserId() );

        return podcast.build();
    }

    @Override
    public Podcast toEntity(PodcastCreateRequest podcastCreateDto) {
        if ( podcastCreateDto == null ) {
            return null;
        }

        Podcast.PodcastBuilder podcast = Podcast.builder();

        podcast.audioFileUrl( podcastCreateDto.getAudioFileUrl() );
        podcast.coverImageUrl( podcastCreateDto.getCoverImageUrl() );
        podcast.description( podcastCreateDto.getDescription() );
        podcast.durationSeconds( podcastCreateDto.getDurationSeconds() );
        podcast.poiId( podcastCreateDto.getPoiId() );
        podcast.title( podcastCreateDto.getTitle() );
        podcast.userId( podcastCreateDto.getUserId() );

        podcast.podcastId( java.util.UUID.randomUUID() );
        podcast.isActive( true );
        podcast.createdAt( java.time.LocalDateTime.now() );
        podcast.updatedAt( java.time.LocalDateTime.now() );

        return podcast.build();
    }

    @Override
    public void updateEntityFromDto(UpdatePodcastRequest podcastUpdateDto, Podcast podcast) {
        if ( podcastUpdateDto == null ) {
            return;
        }

        if ( podcastUpdateDto.getAudioFileUrl() != null ) {
            podcast.setAudioFileUrl( podcastUpdateDto.getAudioFileUrl() );
        }
        if ( podcastUpdateDto.getCoverImageUrl() != null ) {
            podcast.setCoverImageUrl( podcastUpdateDto.getCoverImageUrl() );
        }
        if ( podcastUpdateDto.getDescription() != null ) {
            podcast.setDescription( podcastUpdateDto.getDescription() );
        }
        if ( podcastUpdateDto.getDurationSeconds() != null ) {
            podcast.setDurationSeconds( podcastUpdateDto.getDurationSeconds() );
        }
        if ( podcastUpdateDto.getIsActive() != null ) {
            podcast.setIsActive( podcastUpdateDto.getIsActive() );
        }
        if ( podcastUpdateDto.getTitle() != null ) {
            podcast.setTitle( podcastUpdateDto.getTitle() );
        }

        podcast.setUpdatedAt( java.time.LocalDateTime.now() );
    }
}
