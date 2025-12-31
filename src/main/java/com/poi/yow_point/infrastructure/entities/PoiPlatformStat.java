package com.poi.yow_point.infrastructure.entities;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("poi_platform_stat")
public class PoiPlatformStat {

    @Id
    @Column("stat_id")
    private UUID statId;

    @Column("org_id")
    private UUID orgId;

    @Column("poi_id") // Nullable as per SQL (can be overall org stats)
    private UUID poiId;

    @Column("platform_type")
    private String platformType; // e.g. 'IOS', 'ANDROID', 'LINUX', etc.

    @Column("stat_date")
    private LocalDate statDate;

    @Column("views")
    @Builder.Default
    private Integer views = 0;

    @Column("reviews")
    @Builder.Default
    private Integer reviews = 0;

    @Column("likes")
    @Builder.Default
    private Integer likes = 0;

    @Column("dislikes")
    @Builder.Default
    private Integer dislikes = 0;

    // Transient fields pour les objets relationnels (seront chargés séparément)
    @Transient
    private Organization organization;

    @Transient
    private PointOfInterest pointOfInterest;
}