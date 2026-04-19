package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.Organization;
import com.poi.yow_point.infrastructure.entities.PoiPlatformStat;
import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.presentation.dto.PoiPlatformStatDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T17:19:59+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PoiPlatformStatMapperImpl implements PoiPlatformStatMapper {

    @Override
    public PoiPlatformStatDTO toDTO(PoiPlatformStat poiPlatformStat) {
        if ( poiPlatformStat == null ) {
            return null;
        }

        PoiPlatformStatDTO.PoiPlatformStatDTOBuilder poiPlatformStatDTO = PoiPlatformStatDTO.builder();

        poiPlatformStatDTO.orgId( poiPlatformStat.getOrgId() );
        poiPlatformStatDTO.poiId( poiPlatformStat.getPoiId() );
        poiPlatformStatDTO.dislikes( poiPlatformStat.getDislikes() );
        poiPlatformStatDTO.likes( poiPlatformStat.getLikes() );
        poiPlatformStatDTO.platformType( poiPlatformStat.getPlatformType() );
        poiPlatformStatDTO.reviews( poiPlatformStat.getReviews() );
        poiPlatformStatDTO.statDate( poiPlatformStat.getStatDate() );
        poiPlatformStatDTO.statId( poiPlatformStat.getStatId() );
        poiPlatformStatDTO.views( poiPlatformStat.getViews() );

        return poiPlatformStatDTO.build();
    }

    @Override
    public PoiPlatformStat toEntity(PoiPlatformStatDTO poiPlatformStatDTO) {
        if ( poiPlatformStatDTO == null ) {
            return null;
        }

        PoiPlatformStat.PoiPlatformStatBuilder poiPlatformStat = PoiPlatformStat.builder();

        poiPlatformStat.dislikes( poiPlatformStatDTO.getDislikes() );
        poiPlatformStat.likes( poiPlatformStatDTO.getLikes() );
        poiPlatformStat.orgId( poiPlatformStatDTO.getOrgId() );
        poiPlatformStat.platformType( poiPlatformStatDTO.getPlatformType() );
        poiPlatformStat.poiId( poiPlatformStatDTO.getPoiId() );
        poiPlatformStat.reviews( poiPlatformStatDTO.getReviews() );
        poiPlatformStat.statDate( poiPlatformStatDTO.getStatDate() );
        poiPlatformStat.statId( poiPlatformStatDTO.getStatId() );
        poiPlatformStat.views( poiPlatformStatDTO.getViews() );

        return poiPlatformStat.build();
    }

    @Override
    public PoiPlatformStatDTO toDTOWithRelations(PoiPlatformStat poiPlatformStat) {
        if ( poiPlatformStat == null ) {
            return null;
        }

        PoiPlatformStatDTO.PoiPlatformStatDTOBuilder poiPlatformStatDTO = PoiPlatformStatDTO.builder();

        poiPlatformStatDTO.orgId( poiPlatformStatOrganizationOrganizationId( poiPlatformStat ) );
        poiPlatformStatDTO.poiId( poiPlatformStatPointOfInterestPoiId( poiPlatformStat ) );
        poiPlatformStatDTO.dislikes( poiPlatformStat.getDislikes() );
        poiPlatformStatDTO.likes( poiPlatformStat.getLikes() );
        poiPlatformStatDTO.platformType( poiPlatformStat.getPlatformType() );
        poiPlatformStatDTO.reviews( poiPlatformStat.getReviews() );
        poiPlatformStatDTO.statDate( poiPlatformStat.getStatDate() );
        poiPlatformStatDTO.statId( poiPlatformStat.getStatId() );
        poiPlatformStatDTO.views( poiPlatformStat.getViews() );

        return poiPlatformStatDTO.build();
    }

    private UUID poiPlatformStatOrganizationOrganizationId(PoiPlatformStat poiPlatformStat) {
        if ( poiPlatformStat == null ) {
            return null;
        }
        Organization organization = poiPlatformStat.getOrganization();
        if ( organization == null ) {
            return null;
        }
        UUID organizationId = organization.getOrganizationId();
        if ( organizationId == null ) {
            return null;
        }
        return organizationId;
    }

    private UUID poiPlatformStatPointOfInterestPoiId(PoiPlatformStat poiPlatformStat) {
        if ( poiPlatformStat == null ) {
            return null;
        }
        PointOfInterest pointOfInterest = poiPlatformStat.getPointOfInterest();
        if ( pointOfInterest == null ) {
            return null;
        }
        UUID poiId = pointOfInterest.getPoiId();
        if ( poiId == null ) {
            return null;
        }
        return poiId;
    }
}
