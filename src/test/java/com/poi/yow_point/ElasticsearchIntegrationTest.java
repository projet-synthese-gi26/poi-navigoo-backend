package com.poi.yow_point;

import com.poi.yow_point.application.services.indexing.IndexingService;
import com.poi.yow_point.infrastructure.document.PoiDocument;
import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.infrastructure.repositories.poiDocument.PoiDocumentRepository;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ElasticsearchIntegrationTest {

    @Autowired
    private IndexingService indexingService;

    @MockBean
    private PoiDocumentRepository poiDocumentRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void whenIndexPoi_thenSaveIsCalled() {
        // Given
        PointOfInterest poi = new PointOfInterest();
        poi.setPoiId(UUID.randomUUID());
        poi.setLocationGeog(geometryFactory.createPoint(new Coordinate(0, 0)));
        poi.setPoiName("Test POI");

        when(poiDocumentRepository.save(any(PoiDocument.class))).thenReturn(Mono.just(new PoiDocument()));

        // When
        indexingService.indexPoi(poi).block();

        // Then
        verify(poiDocumentRepository).save(any(PoiDocument.class));
    }

    @Test
    void whenDeletePoi_thenDeleteByIdIsCalled() {
        // Given
        PointOfInterest poi = new PointOfInterest();
        UUID poiId = UUID.randomUUID();
        poi.setPoiId(poiId);
        poi.setLocationGeog(geometryFactory.createPoint(new Coordinate(0, 0)));

        when(poiDocumentRepository.deleteById(poiId.toString())).thenReturn(Mono.empty());

        // When
        indexingService.deletePoi(poi).block();

        // Then
        verify(poiDocumentRepository).deleteById(poiId.toString());
    }
}
