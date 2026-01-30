package com.poi.yow_point.infrastructure.kafka;

import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    public KafkaConsumerService() {
    }

    @KafkaListener(topics = "poi-created", groupId = "yow_point_group")
    public void listenPoiCreated(PointOfInterestDTO poiDTO) {
        log.info("Received POI created event: {}", poiDTO);
    }

    @KafkaListener(topics = "poi-updated", groupId = "yow_point_group")
    public void listenPoiUpdated(PointOfInterestDTO poiDTO) {
        log.info("Received POI updated event: {}", poiDTO);
    }

    @KafkaListener(topics = "poi-deleted", groupId = "yow_point_group")
    public void listenPoiDeleted(PointOfInterestDTO poiDTO) {
        log.info("Received POI deleted event: {}", poiDTO);
    }
}
