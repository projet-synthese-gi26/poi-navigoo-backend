package com.poi.yow_point.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poi.yow_point.application.model.PoiCategory;
import com.poi.yow_point.application.model.PoiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePoiDTO {

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("created_by_user_id")
    private UUID createdByUserId;

    @JsonProperty("poi_name")
    private String poiName;

    @JsonProperty("poi_type")
    private PoiType poiType;

    @JsonProperty("poi_category")
    private PoiCategory poiCategory;

    @JsonProperty("poi_long_name")
    private String poiLongName;

    @JsonProperty("poi_short_name")
    private String poiShortName;

    @JsonProperty("poi_friendly_name")
    private String poiFriendlyName;

    @JsonProperty("poi_description")
    private String poiDescription;

    @JsonProperty("poi_logo")
    private String poiLogo;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("address_street_number")
    private String addressStreetNumber;

    @JsonProperty("address_street_name")
    private String addressStreetName;

    @JsonProperty("address_city")
    private String addressCity;

    @JsonProperty("address_state_province")
    private String addressStateProvince;

    @JsonProperty("address_postal_code")
    private String addressPostalCode;

    @JsonProperty("address_country")
    private String addressCountry;

    @JsonProperty("address_informal")
    private String addressInformal;

    @JsonProperty("website_url")
    private String websiteUrl;

    @JsonProperty("operation_time_plan")
    private Map<String, Object> operationTimePlan;

    @JsonProperty("poi_contacts")
    private Map<String, Object> poiContacts;

    @JsonProperty("poi_images_urls")
    private List<String> poiImagesUrls;

    @JsonProperty("poi_amenities")
    private List<String> poiAmenities;

    @JsonProperty("poi_keywords")
    private List<String> poiKeywords;

    @JsonProperty("poi_type_tags")
    private List<String> poiTypeTags;
}
