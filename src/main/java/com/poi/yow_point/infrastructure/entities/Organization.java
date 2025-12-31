package com.poi.yow_point.infrastructure.entities;

import com.poi.yow_point.application.model.OrganizationType;
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
@Table("organization")
public class Organization {

    @Id
    @Column("organization_id")
    private UUID organizationId;

    @Column("org_name")
    private String organizationName;

    @Column("org_code")
    private String orgCode;

    @Column("org_type")
    private OrganizationType orgType;

    @CreatedDate
    @Column("created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

}
