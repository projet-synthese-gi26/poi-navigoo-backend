package com.poi.yow_point.infrastructure.kafka;

import com.poi.yow_point.application.mappers.MapperUtils;
import com.poi.yow_point.application.mappers.PointOfInterestMapper;
import com.poi.yow_point.application.services.indexing.IndexingService;
import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final IndexingService indexingService;
    private final PointOfInterestMapper pointOfInterestMapper;
    private final MapperUtils mapperUtils;

    public KafkaConsumerService(IndexingService indexingService, PointOfInterestMapper pointOfInterestMapper, MapperUtils mapperUtils) {
        this.indexingService = indexingService;
        this.pointOfInterestMapper = pointOfInterestMapper;
        this.mapperUtils = mapperUtils;
    }

    @KafkaListener(topics = "poi-created", groupId = "yow_point_group")
    public void listenPoiCreated(PointOfInterestDTO poiDTO) {
        log.info("Received POI created event: {}", poiDTO);
        PointOfInterest poi = pointOfInterestMapper.toEntity(poiDTO, mapperUtils);
        poi.setPoiId(poiDTO.getPoiId());
        indexingService.indexPoi(poi).subscribe();
    }

    @KafkaListener(topics = "poi-updated", groupId = "yow_point_group")
    public void listenPoiUpdated(PointOfInterestDTO poiDTO) {
        log.info("Received POI updated event: {}", poiDTO);
        PointOfInterest poi = pointOfInterestMapper.toEntity(poiDTO, mapperUtils);
        poi.setPoiId(poiDTO.getPoiId());
        indexingService.indexPoi(poi).subscribe();
    }

    @KafkaListener(topics = "poi-deleted", groupId = "yow_point_group")
    public void listenPoiDeleted(PointOfInterestDTO poiDTO) {
        log.info("Received POI deleted event: {}", poiDTO);
        PointOfInterest poi = pointOfInterestMapper.toEntity(poiDTO, mapperUtils);
        poi.setPoiId(poiDTO.getPoiId());
        indexingService.deletePoi(poi).subscribe();
    }
}
