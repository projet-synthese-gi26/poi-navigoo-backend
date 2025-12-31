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
@Table("poi_review")
public class PoiReview {

    @Id
    @Column("review_id")
    private UUID reviewId;

    @Column("poi_id")
    private UUID poiId;

    @Column("user_id")
    private UUID userId;

    @Column("organization_id")
    private UUID organizationId;

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
}