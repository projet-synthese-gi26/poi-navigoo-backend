package com.poi.yow_point.presentation.dto.podcastDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdatePodcastRequest {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    @JsonProperty("audio_file_url")
    private String audioFileUrl;

    @JsonProperty("duration_seconds")
    private Integer durationSeconds;

    @JsonProperty("is_active")
    private Boolean isActive;
}
