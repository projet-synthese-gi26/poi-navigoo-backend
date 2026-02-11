package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.presentation.dto.CreatePoiDTO;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.UpdatePoiDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-11T15:58:55+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class PointOfInterestMapperImpl implements PointOfInterestMapper {

    @Autowired
    private MapperUtils mapperUtils;

    @Override
    public PointOfInterestDTO toDto(PointOfInterest entity) {
        if ( entity == null ) {
            return null;
        }

        PointOfInterestDTO.PointOfInterestDTOBuilder pointOfInterestDTO = PointOfInterestDTO.builder();

        pointOfInterestDTO.latitude( mapperUtils.pointToLatitude( entity.getLocationGeog() ) );
        pointOfInterestDTO.longitude( mapperUtils.pointToLongitude( entity.getLocationGeog() ) );
        pointOfInterestDTO.poiImagesUrls( mapperUtils.stringToList( entity.getPoiImagesUrls() ) );
        pointOfInterestDTO.poiAmenities( mapperUtils.stringToList( entity.getPoiAmenities() ) );
        pointOfInterestDTO.poiKeywords( mapperUtils.stringToList( entity.getPoiKeywords() ) );
        pointOfInterestDTO.poiTypeTags( mapperUtils.stringToList( entity.getPoiTypeTags() ) );
        pointOfInterestDTO.operationTimePlan( mapperUtils.jsonToMap( entity.getOperationTimePlan() ) );
        pointOfInterestDTO.poiContacts( mapperUtils.jsonToMap( entity.getPoiContacts() ) );
        pointOfInterestDTO.poiId( entity.getPoiId() );
        pointOfInterestDTO.organizationId( entity.getOrganizationId() );
        pointOfInterestDTO.createdByUserId( entity.getCreatedByUserId() );
        pointOfInterestDTO.poiName( entity.getPoiName() );
        pointOfInterestDTO.poiType( entity.getPoiType() );
        pointOfInterestDTO.poiCategory( entity.getPoiCategory() );
        pointOfInterestDTO.poiLongName( entity.getPoiLongName() );
        pointOfInterestDTO.poiShortName( entity.getPoiShortName() );
        pointOfInterestDTO.poiFriendlyName( entity.getPoiFriendlyName() );
        pointOfInterestDTO.poiDescription( entity.getPoiDescription() );
        pointOfInterestDTO.poiLogo( entity.getPoiLogo() );
        pointOfInterestDTO.addressStreetNumber( entity.getAddressStreetNumber() );
        pointOfInterestDTO.addressStreetName( entity.getAddressStreetName() );
        pointOfInterestDTO.addressCity( entity.getAddressCity() );
        pointOfInterestDTO.addressStateProvince( entity.getAddressStateProvince() );
        pointOfInterestDTO.addressPostalCode( entity.getAddressPostalCode() );
        pointOfInterestDTO.addressCountry( entity.getAddressCountry() );
        pointOfInterestDTO.addressInformal( entity.getAddressInformal() );
        pointOfInterestDTO.websiteUrl( entity.getWebsiteUrl() );
        pointOfInterestDTO.popularityScore( entity.getPopularityScore() );
        pointOfInterestDTO.isActive( entity.getIsActive() );
        pointOfInterestDTO.deactivationReason( entity.getDeactivationReason() );
        pointOfInterestDTO.deactivatedByUserId( entity.getDeactivatedByUserId() );
        pointOfInterestDTO.status( entity.getStatus() );
        pointOfInterestDTO.approuvedByUserId( entity.getApprouvedByUserId() );
        pointOfInterestDTO.createdAt( entity.getCreatedAt() );
        pointOfInterestDTO.updatedByUserId( entity.getUpdatedByUserId() );
        pointOfInterestDTO.updatedAt( entity.getUpdatedAt() );

        return pointOfInterestDTO.build();
    }

    @Override
    public PointOfInterest toEntity(CreatePoiDTO dto, MapperUtils mapperUtils) {
        if ( dto == null ) {
            return null;
        }

        PointOfInterest.PointOfInterestBuilder pointOfInterest = PointOfInterest.builder();

        pointOfInterest.poiImagesUrls( mapperUtils.listToString( dto.getPoiImagesUrls() ) );
        pointOfInterest.poiAmenities( mapperUtils.listToString( dto.getPoiAmenities() ) );
        pointOfInterest.poiKeywords( mapperUtils.listToString( dto.getPoiKeywords() ) );
        pointOfInterest.poiTypeTags( mapperUtils.listToString( dto.getPoiTypeTags() ) );
        pointOfInterest.operationTimePlan( mapperUtils.mapToJson( dto.getOperationTimePlan() ) );
        pointOfInterest.poiContacts( mapperUtils.mapToJson( dto.getPoiContacts() ) );
        pointOfInterest.organizationId( dto.getOrganizationId() );
        pointOfInterest.createdByUserId( dto.getCreatedByUserId() );
        pointOfInterest.poiName( dto.getPoiName() );
        pointOfInterest.poiType( dto.getPoiType() );
        pointOfInterest.poiCategory( dto.getPoiCategory() );
        pointOfInterest.poiLongName( dto.getPoiLongName() );
        pointOfInterest.poiShortName( dto.getPoiShortName() );
        pointOfInterest.poiFriendlyName( dto.getPoiFriendlyName() );
        pointOfInterest.poiDescription( dto.getPoiDescription() );
        pointOfInterest.poiLogo( dto.getPoiLogo() );
        pointOfInterest.addressStreetNumber( dto.getAddressStreetNumber() );
        pointOfInterest.addressStreetName( dto.getAddressStreetName() );
        pointOfInterest.addressCity( dto.getAddressCity() );
        pointOfInterest.addressStateProvince( dto.getAddressStateProvince() );
        pointOfInterest.addressPostalCode( dto.getAddressPostalCode() );
        pointOfInterest.addressCountry( dto.getAddressCountry() );
        pointOfInterest.addressInformal( dto.getAddressInformal() );
        pointOfInterest.websiteUrl( dto.getWebsiteUrl() );

        pointOfInterest.locationGeog( mapperUtils.coordinatesToPoint(dto.getLatitude(), dto.getLongitude()) );

        return pointOfInterest.build();
    }

    @Override
    public void updateEntityFromDto(PointOfInterest entity, UpdatePoiDTO dto, MapperUtils mapperUtils) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getPoiImagesUrls() != null ) {
            entity.setPoiImagesUrls( mapperUtils.listToString( dto.getPoiImagesUrls() ) );
        }
        if ( dto.getPoiAmenities() != null ) {
            entity.setPoiAmenities( mapperUtils.listToString( dto.getPoiAmenities() ) );
        }
        if ( dto.getPoiKeywords() != null ) {
            entity.setPoiKeywords( mapperUtils.listToString( dto.getPoiKeywords() ) );
        }
        if ( dto.getPoiTypeTags() != null ) {
            entity.setPoiTypeTags( mapperUtils.listToString( dto.getPoiTypeTags() ) );
        }
        if ( dto.getOperationTimePlan() != null ) {
            entity.setOperationTimePlan( mapperUtils.mapToJson( dto.getOperationTimePlan() ) );
        }
        if ( dto.getPoiContacts() != null ) {
            entity.setPoiContacts( mapperUtils.mapToJson( dto.getPoiContacts() ) );
        }
        if ( dto.getPoiName() != null ) {
            entity.setPoiName( dto.getPoiName() );
        }
        if ( dto.getPoiType() != null ) {
            entity.setPoiType( dto.getPoiType() );
        }
        if ( dto.getPoiCategory() != null ) {
            entity.setPoiCategory( dto.getPoiCategory() );
        }
        if ( dto.getPoiLongName() != null ) {
            entity.setPoiLongName( dto.getPoiLongName() );
        }
        if ( dto.getPoiShortName() != null ) {
            entity.setPoiShortName( dto.getPoiShortName() );
        }
        if ( dto.getPoiFriendlyName() != null ) {
            entity.setPoiFriendlyName( dto.getPoiFriendlyName() );
        }
        if ( dto.getPoiDescription() != null ) {
            entity.setPoiDescription( dto.getPoiDescription() );
        }
        if ( dto.getPoiLogo() != null ) {
            entity.setPoiLogo( dto.getPoiLogo() );
        }
        if ( dto.getAddressStreetNumber() != null ) {
            entity.setAddressStreetNumber( dto.getAddressStreetNumber() );
        }
        if ( dto.getAddressStreetName() != null ) {
            entity.setAddressStreetName( dto.getAddressStreetName() );
        }
        if ( dto.getAddressCity() != null ) {
            entity.setAddressCity( dto.getAddressCity() );
        }
        if ( dto.getAddressStateProvince() != null ) {
            entity.setAddressStateProvince( dto.getAddressStateProvince() );
        }
        if ( dto.getAddressPostalCode() != null ) {
            entity.setAddressPostalCode( dto.getAddressPostalCode() );
        }
        if ( dto.getAddressCountry() != null ) {
            entity.setAddressCountry( dto.getAddressCountry() );
        }
        if ( dto.getAddressInformal() != null ) {
            entity.setAddressInformal( dto.getAddressInformal() );
        }
        if ( dto.getWebsiteUrl() != null ) {
            entity.setWebsiteUrl( dto.getWebsiteUrl() );
        }

        entity.setLocationGeog( mapperUtils.coordinatesToPoint(dto.getLatitude(), dto.getLongitude()) );

        setUpdateTimestamp( entity );
    }
}
