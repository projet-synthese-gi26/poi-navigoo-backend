package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PoiAccessLog;
import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-19T17:19:59+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PoiAccessLogMapperImpl implements PoiAccessLogMapper {

    @Autowired
    private MapperUtils mapperUtils;

    @Override
    public PoiAccessLogDTO toDto(PoiAccessLog entity) {
        if ( entity == null ) {
            return null;
        }

        PoiAccessLogDTO.PoiAccessLogDTOBuilder poiAccessLogDTO = PoiAccessLogDTO.builder();

        poiAccessLogDTO.accessDatetime( entity.getAccessDatetime() );
        poiAccessLogDTO.accessId( entity.getAccessId() );
        poiAccessLogDTO.accessType( entity.getAccessType() );
        if ( entity.hasMetadata() ) {
            poiAccessLogDTO.metadata( mapperUtils.jsonNodeToMap( entity.getMetadata() ) );
        }
        poiAccessLogDTO.organizationId( entity.getOrganizationId() );
        poiAccessLogDTO.platformType( entity.getPlatformType() );
        poiAccessLogDTO.poiId( entity.getPoiId() );
        poiAccessLogDTO.userId( entity.getUserId() );

        return poiAccessLogDTO.build();
    }

    @Override
    public PoiAccessLog toEntity(PoiAccessLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PoiAccessLog.PoiAccessLogBuilder poiAccessLog = PoiAccessLog.builder();

        poiAccessLog.accessDatetime( dto.getAccessDatetime() );
        poiAccessLog.accessId( dto.getAccessId() );
        poiAccessLog.accessType( dto.getAccessType() );
        poiAccessLog.organizationId( dto.getOrganizationId() );
        poiAccessLog.platformType( dto.getPlatformType() );
        poiAccessLog.poiId( dto.getPoiId() );
        poiAccessLog.userId( dto.getUserId() );

        return poiAccessLog.build();
    }
}
