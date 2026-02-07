package com.poi.yow_point.infrastructure.entities;

import com.poi.yow_point.application.model.PoiCategory;
import com.poi.yow_point.application.model.PoiType;
import com.poi.yow_point.application.model.PoiStatus;
import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("point_of_interest")
public class PointOfInterest {

    @Id
    @Column("poi_id")
    private UUID poiId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("created_by_user_id")
    private UUID createdByUserId;

    @Column("poi_name")
    private String poiName;

    @Column("poi_type")
    private PoiType poiType;

    @Column("poi_category")
    private PoiCategory poiCategory;

    @Column("poi_long_name")
    private String poiLongName;

    @Column("poi_short_name")
    private String poiShortName;

    @Column("poi_friendly_name")
    private String poiFriendlyName;

    @Column("poi_description")
    private String poiDescription;

    @Column("poi_logo")
    private String poiLogo;

    @Column("location_geog")
    private Point locationGeog;

    @Column("address_street_number")
    private String addressStreetNumber;

    @Column("address_street_name")
    private String addressStreetName;

    @Column("address_city")
    private String addressCity;

    @Column("address_state_province")
    private String addressStateProvince;

    @Column("address_postal_code")
    private String addressPostalCode;

    @Column("address_country")
    private String addressCountry;

    @Column("address_informal")
    private String addressInformal;

    @Column("website_url")
    private String websiteUrl;

    @Column("operation_time_plan")
    private Json operationTimePlan;

    @Column("poi_contacts")
    private Json poiContacts;

    @Column("poi_images_urls")
    private String poiImagesUrls;

    @Column("poi_amenities")
    private String poiAmenities;

    @Column("poi_keywords")
    private String poiKeywords;

    @Column("poi_type_tags")
    private String poiTypeTags;

    @Column("popularity_score")
    private Float popularityScore;

    @Column("is_active")
    private Boolean isActive;

    @Column("deactivation_reason")
    private String deactivationReason;

    @Column("deactivated_by_user_id")
    private UUID deactivatedByUserId;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_by_user_id")
    private UUID updatedByUserId;


    @Column("status")
    private PoiStatus status;

    @Column("approuved_by_user_id")
    private UUID approuvedByUserId;

    @Column("updated_at")
    private Instant updatedAt;



    // --- Helper methods for comma-separated string fields ---

    public List<String> getPoiImagesUrlsList() {
        if (poiImagesUrls == null || poiImagesUrls.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(poiImagesUrls.split("\\s*,\\s*"));
    }

    public void setPoiImagesUrlsList(List<String> urls) {
        this.poiImagesUrls = (urls != null && !urls.isEmpty()) ? String.join(",", urls) : null;
    }

    public List<String> getPoiAmenitiesList() {
        if (poiAmenities == null || poiAmenities.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(poiAmenities.split("\\s*,\\s*"));
    }

    public void setPoiAmenitiesList(List<String> amenities) {
        this.poiAmenities = (amenities != null && !amenities.isEmpty()) ? String.join(",", amenities) : null;
    }

    public List<String> getPoiKeywordsList() {
        if (poiKeywords == null || poiKeywords.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(poiKeywords.split("\\s*,\\s*"));
    }

    public void setPoiKeywordsList(List<String> keywords) {
        this.poiKeywords = (keywords != null && !keywords.isEmpty()) ? String.join(",", keywords) : null;
    }

    public List<String> getPoiTypeTagsList() {
        if (poiTypeTags == null || poiTypeTags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(poiTypeTags.split("\\s*,\\s*"));
    }

    public void setPoiTypeTagsList(List<String> tags) {
        this.poiTypeTags = (tags != null && !tags.isEmpty()) ? String.join(",", tags) : null;
    }
}
