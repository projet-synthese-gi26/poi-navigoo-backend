package com.poi.yow_point.presentation.dto;

import com.poi.yow_point.application.model.OrganizationType;
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
public class OrganizationDTO {
    private UUID organizationId;
    private String organizationName;
    private String orgCode;
    private OrganizationType orgType;
    private OffsetDateTime createdAt;
    private Boolean isActive;
}
