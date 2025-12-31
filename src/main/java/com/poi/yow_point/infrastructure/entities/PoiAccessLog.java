package com.poi.yow_point.infrastructure.entities;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("poi_access_log")
public class PoiAccessLog {

    @Id
    @Column("access_id")
    private UUID accessId;

    @Column("poi_id")
    private UUID poiId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("platform_type")
    private String platformType;

    @Column("user_id")
    private UUID userId;

    @Column("access_type")
    private String accessType;

    @CreatedDate
    @Column("access_datetime")
    @Builder.Default
    private OffsetDateTime accessDatetime = OffsetDateTime.now();

    @Column("metadata")
    private JsonNode metadata;

    // Méthodes utilitaires
    public boolean hasMetadata() {
        return metadata != null && !metadata.isNull() && !metadata.isEmpty();
    }

    // Setter personnalisé pour gérer les valeurs null
    public void setMetadata(JsonNode metadata) {
        if (metadata != null && metadata.isNull()) {
            this.metadata = null;
        } else {
            this.metadata = metadata;
        }
    }

}