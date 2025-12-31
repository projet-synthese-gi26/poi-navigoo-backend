package com.poi.yow_point.presentation.dto.podcastDto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PodcastDTO {
    @JsonProperty("podcast_id")
    private UUID podcastId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("poi_id")
    private UUID poiId;

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

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
