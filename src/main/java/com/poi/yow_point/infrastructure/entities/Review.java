package com.poi.yow_point.infrastructure.entities;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("review")
public class Review implements org.springframework.data.domain.Persistable<UUID> {

    @Id
    @Column("review_id")
    private UUID reviewId;

    @Column("poi_id")
    private UUID poiId;

    @Column("blog_id")
    private UUID blogId;

    @Column("podcast_id")
    private UUID podcastId;

    @Column("user_id")
    private UUID userId;

    @Column("platform_type")
    private String platformType; // Plateforme source du review

    @Column("rating") // CHECK constraint (rating >= 1 AND rating <= 5) handled by DB
    private Integer rating;

    @Column("review_text")
    private String reviewText;

    @CreatedDate
    @Column("created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column("likes")
    @Builder.Default
    private Integer likes = 0;

    @Column("dislikes")
    @Builder.Default
    private Integer dislikes = 0;

    @org.springframework.data.annotation.Transient
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private boolean isNew = false;

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public UUID getId() {
        return reviewId;
    }

    @Override
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isNew() {
        return isNew || reviewId == null;
    }
}