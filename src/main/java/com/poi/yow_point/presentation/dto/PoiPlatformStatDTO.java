package com.poi.yow_point.presentation.dto;

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
public class PoiPlatformStatDTO {
    private UUID statId;
    private UUID orgId;
    private UUID poiId;
    private String platformType;
    private LocalDate statDate;
    private Integer views;
    private Integer reviews;
    private Integer likes;
    private Integer dislikes;
}