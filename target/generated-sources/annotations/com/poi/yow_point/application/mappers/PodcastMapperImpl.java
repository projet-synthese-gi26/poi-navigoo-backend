package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Podcast;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastCreateRequest;
import com.poi.yow_point.presentation.dto.podcastDto.PodcastDTO;
import com.poi.yow_point.presentation.dto.podcastDto.UpdatePodcastRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-02T14:20:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class PodcastMapperImpl implements PodcastMapper {

    @Override
    public PodcastDTO toDto(Podcast podcast) {
        if ( podcast == null ) {
            return null;
        }

        PodcastDTO.PodcastDTOBuilder podcastDTO = PodcastDTO.builder();

        podcastDTO.podcastId( podcast.getPodcastId() );
        podcastDTO.userId( podcast.getUserId() );
        podcastDTO.poiId( podcast.getPoiId() );
        podcastDTO.title( podcast.getTitle() );
        podcastDTO.description( podcast.getDescription() );
        podcastDTO.coverImageUrl( podcast.getCoverImageUrl() );
        podcastDTO.audioFileUrl( podcast.getAudioFileUrl() );
        podcastDTO.durationSeconds( podcast.getDurationSeconds() );
        podcastDTO.isActive( podcast.getIsActive() );
        podcastDTO.createdAt( podcast.getCreatedAt() );
        podcastDTO.updatedAt( podcast.getUpdatedAt() );

        return podcastDTO.build();
    }

    @Override
    public Podcast toEntity(PodcastDTO podcastDto) {
        if ( podcastDto == null ) {
            return null;
        }

        Podcast.PodcastBuilder podcast = Podcast.builder();

        podcast.podcastId( podcastDto.getPodcastId() );
        podcast.userId( podcastDto.getUserId() );
        podcast.poiId( podcastDto.getPoiId() );
        podcast.title( podcastDto.getTitle() );
        podcast.description( podcastDto.getDescription() );
        podcast.coverImageUrl( podcastDto.getCoverImageUrl() );
        podcast.audioFileUrl( podcastDto.getAudioFileUrl() );
        podcast.durationSeconds( podcastDto.getDurationSeconds() );
        podcast.isActive( podcastDto.getIsActive() );
        podcast.createdAt( podcastDto.getCreatedAt() );
        podcast.updatedAt( podcastDto.getUpdatedAt() );

        return podcast.build();
    }

    @Override
    public Podcast toEntity(PodcastCreateRequest podcastCreateDto) {
        if ( podcastCreateDto == null ) {
            return null;
        }

        Podcast.PodcastBuilder podcast = Podcast.builder();

        podcast.userId( podcastCreateDto.getUserId() );
        podcast.poiId( podcastCreateDto.getPoiId() );
        podcast.title( podcastCreateDto.getTitle() );
        podcast.description( podcastCreateDto.getDescription() );
        podcast.coverImageUrl( podcastCreateDto.getCoverImageUrl() );
        podcast.audioFileUrl( podcastCreateDto.getAudioFileUrl() );
        podcast.durationSeconds( podcastCreateDto.getDurationSeconds() );

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

        if ( podcastUpdateDto.getTitle() != null ) {
            podcast.setTitle( podcastUpdateDto.getTitle() );
        }
        if ( podcastUpdateDto.getDescription() != null ) {
            podcast.setDescription( podcastUpdateDto.getDescription() );
        }
        if ( podcastUpdateDto.getCoverImageUrl() != null ) {
            podcast.setCoverImageUrl( podcastUpdateDto.getCoverImageUrl() );
        }
        if ( podcastUpdateDto.getAudioFileUrl() != null ) {
            podcast.setAudioFileUrl( podcastUpdateDto.getAudioFileUrl() );
        }
        if ( podcastUpdateDto.getDurationSeconds() != null ) {
            podcast.setDurationSeconds( podcastUpdateDto.getDurationSeconds() );
        }
        if ( podcastUpdateDto.getIsActive() != null ) {
            podcast.setIsActive( podcastUpdateDto.getIsActive() );
        }

        podcast.setUpdatedAt( java.time.LocalDateTime.now() );
    }
}
