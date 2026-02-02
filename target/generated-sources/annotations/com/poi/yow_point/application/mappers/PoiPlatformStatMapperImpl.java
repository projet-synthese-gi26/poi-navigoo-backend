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
    date = "2026-02-02T14:20:16+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
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
        poiPlatformStatDTO.statId( poiPlatformStat.getStatId() );
        poiPlatformStatDTO.platformType( poiPlatformStat.getPlatformType() );
        poiPlatformStatDTO.statDate( poiPlatformStat.getStatDate() );
        poiPlatformStatDTO.views( poiPlatformStat.getViews() );
        poiPlatformStatDTO.reviews( poiPlatformStat.getReviews() );
        poiPlatformStatDTO.likes( poiPlatformStat.getLikes() );
        poiPlatformStatDTO.dislikes( poiPlatformStat.getDislikes() );

        return poiPlatformStatDTO.build();
    }

    @Override
    public PoiPlatformStat toEntity(PoiPlatformStatDTO poiPlatformStatDTO) {
        if ( poiPlatformStatDTO == null ) {
            return null;
        }

        PoiPlatformStat.PoiPlatformStatBuilder poiPlatformStat = PoiPlatformStat.builder();

        poiPlatformStat.statId( poiPlatformStatDTO.getStatId() );
        poiPlatformStat.orgId( poiPlatformStatDTO.getOrgId() );
        poiPlatformStat.poiId( poiPlatformStatDTO.getPoiId() );
        poiPlatformStat.platformType( poiPlatformStatDTO.getPlatformType() );
        poiPlatformStat.statDate( poiPlatformStatDTO.getStatDate() );
        poiPlatformStat.views( poiPlatformStatDTO.getViews() );
        poiPlatformStat.reviews( poiPlatformStatDTO.getReviews() );
        poiPlatformStat.likes( poiPlatformStatDTO.getLikes() );
        poiPlatformStat.dislikes( poiPlatformStatDTO.getDislikes() );

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
        poiPlatformStatDTO.statId( poiPlatformStat.getStatId() );
        poiPlatformStatDTO.platformType( poiPlatformStat.getPlatformType() );
        poiPlatformStatDTO.statDate( poiPlatformStat.getStatDate() );
        poiPlatformStatDTO.views( poiPlatformStat.getViews() );
        poiPlatformStatDTO.reviews( poiPlatformStat.getReviews() );
        poiPlatformStatDTO.likes( poiPlatformStat.getLikes() );
        poiPlatformStatDTO.dislikes( poiPlatformStat.getDislikes() );

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
