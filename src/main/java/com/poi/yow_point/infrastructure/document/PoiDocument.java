package com.poi.yow_point.infrastructure.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "poi")
public class PoiDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private UUID poiId;

    @Field(type = FieldType.Keyword)
    private UUID organizationId;

    @Field(type = FieldType.Keyword)
    private UUID townId;

    @Field(type = FieldType.Keyword)
    private UUID createdByUserId;

    @Field(type = FieldType.Text)
    private String poiName;

    @Field(type = FieldType.Keyword)
    private String poiType;

    @Field(type = FieldType.Keyword)
    private String poiCategory;

    @Field(type = FieldType.Text)
    private String poiLongName;

    @Field(type = FieldType.Text)
    private String poiShortName;

    @Field(type = FieldType.Text)
    private String poiFriendlyName;

    @Field(type = FieldType.Text)
    private String poiDescription;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Text)
    private String addressStreetNumber;

    @Field(type = FieldType.Text)
    private String addressStreetName;

    @Field(type = FieldType.Text)
    private String addressCity;

    @Field(type = FieldType.Keyword)
    private String addressStateProvince;

    @Field(type = FieldType.Keyword)
    private String addressPostalCode;

    @Field(type = FieldType.Keyword)
    private String addressCountry;

    @Field(type = FieldType.Text)
    private String addressInformal;

    @Field(type = FieldType.Keyword)
    private String websiteUrl;

    @Field(type = FieldType.Object)
    private Object operationTimePlan;

    @Field(type = FieldType.Object)
    private Object poiContacts;

    @Field(type = FieldType.Keyword)
    private List<String> poiImagesUrls;

    @Field(type = FieldType.Keyword)
    private List<String> poiAmenities;

    @Field(type = FieldType.Keyword)
    private List<String> poiKeywords;

    @Field(type = FieldType.Keyword)
    private List<String> poiTypeTags;

    @Field(type = FieldType.Float)
    private Float popularityScore;

    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    @Field(type = FieldType.Text)
    private String deactivationReason;

    @Field(type = FieldType.Keyword)
    private UUID deactivatedByUserId;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Keyword)
    private UUID updatedByUserId;

    @Field(type = FieldType.Date)
    private Instant updatedAt;
}
