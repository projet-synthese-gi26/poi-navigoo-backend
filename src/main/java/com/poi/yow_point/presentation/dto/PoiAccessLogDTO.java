package com.poi.yow_point.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoiAccessLogDTO {

    private UUID accessId;
    private UUID poiId;
    private UUID organizationId;
    private String platformType;
    private UUID userId;
    private String accessType;
    private OffsetDateTime accessDatetime;
    private Map<String, Object> metadata;

}