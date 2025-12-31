package com.poi.yow_point.infrastructure.repositories.poiAccessLog;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
//import org.springframework.stereotype.Repository;

import com.poi.yow_point.infrastructure.entities.PoiAccessLog;
import java.util.UUID;

public interface PoiAccessLogRepository extends R2dbcRepository<PoiAccessLog, UUID>, PoiAccessLogRepositoryCustom {

}