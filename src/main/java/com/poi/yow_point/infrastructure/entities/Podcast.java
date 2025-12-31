package com.poi.yow_point.infrastructure.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("podcast")
public class Podcast {
    @Id
    @Column("podcast_id")
    private UUID podcastId;

    @Column("user_id")
    private UUID userId;

    @Column("poi_id")
    private UUID poiId;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("cover_image_url")
    private String coverImageUrl;

    @Column("audio_file_url")
    private String audioFileUrl;

    @Column("duration_seconds")
    private Integer durationSeconds;

    @Column("is_active")
    private Boolean isActive;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}