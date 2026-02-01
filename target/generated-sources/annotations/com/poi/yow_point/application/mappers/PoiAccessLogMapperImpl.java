package com.poi.yow_point.application.mappers;

import com.poi.yow_point.infrastructure.entities.PoiAccessLog;
import com.poi.yow_point.presentation.dto.PoiAccessLogDTO;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-01T22:23:27+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
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

        poiAccessLogDTO.accessId( entity.getAccessId() );
        poiAccessLogDTO.poiId( entity.getPoiId() );
        poiAccessLogDTO.organizationId( entity.getOrganizationId() );
        poiAccessLogDTO.platformType( entity.getPlatformType() );
        poiAccessLogDTO.userId( entity.getUserId() );
        poiAccessLogDTO.accessType( entity.getAccessType() );
        poiAccessLogDTO.accessDatetime( entity.getAccessDatetime() );
        if ( entity.hasMetadata() ) {
            poiAccessLogDTO.metadata( mapperUtils.jsonNodeToMap( entity.getMetadata() ) );
        }

        return poiAccessLogDTO.build();
    }

    @Override
    public PoiAccessLog toEntity(PoiAccessLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PoiAccessLog.PoiAccessLogBuilder poiAccessLog = PoiAccessLog.builder();

        poiAccessLog.accessId( dto.getAccessId() );
        poiAccessLog.poiId( dto.getPoiId() );
        poiAccessLog.organizationId( dto.getOrganizationId() );
        poiAccessLog.platformType( dto.getPlatformType() );
        poiAccessLog.userId( dto.getUserId() );
        poiAccessLog.accessType( dto.getAccessType() );
        poiAccessLog.accessDatetime( dto.getAccessDatetime() );

        return poiAccessLog.build();
    }
}
