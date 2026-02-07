package com.poi.yow_point.presentation.controllers;

import com.poi.yow_point.application.model.PoiCategory;
import com.poi.yow_point.application.model.PoiType;
import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.infrastructure.kafka.KafkaProducerService;
import com.poi.yow_point.infrastructure.repositories.PointOfInterest.PointOfInterestRepository;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "postgres-test"})
class PointOfInterestControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PointOfInterestRepository pointOfInterestRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private com.poi.yow_point.infrastructure.kafka.KafkaConsumerService kafkaConsumerService;

    @MockBean
    private org.springframework.data.redis.core.ReactiveRedisTemplate<String, PointOfInterestDTO> redisTemplate;

    private PointOfInterestDTO testPoiDto;
    private final UUID organizationId = UUID.randomUUID();
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        testPoiDto = PointOfInterestDTO.builder()
                .organizationId(organizationId)
                .createdByUserId(UUID.randomUUID())
                .poiName("Test POI")
                .poiType(PoiType.RESTAURANT)
                .poiCategory(PoiCategory.FOOD_DRINK)
                .poiDescription("A great test restaurant")
                .latitude(3.8480)
                .longitude(11.5021)
                .addressCity("Yaoundé")
                .poiKeywords(Collections.singletonList("test"))
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @MockBean
    private com.poi.yow_point.application.services.appUser.AppUserService appUserService;

    @MockBean
    private com.poi.yow_point.application.services.notification.NotificationService notificationService;

    @Test
    void testFullPoiLifecycle() {
        UUID poiId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        com.poi.yow_point.presentation.dto.CreatePoiDTO createDto = com.poi.yow_point.presentation.dto.CreatePoiDTO.builder()
                .organizationId(organizationId)
                .createdByUserId(userId)
                .poiName("Test POI")
                .poiType(PoiType.RESTAURANT)
                .poiCategory(PoiCategory.FOOD_DRINK)
                .poiDescription("A great test restaurant")
                .latitude(3.8480)
                .longitude(11.5021)
                .addressCity("Yaoundé")
                .poiKeywords(Collections.singletonList("test"))
                .build();

        // --- Mocking Repository Behavior ---
        PointOfInterest savedPoi = new PointOfInterest();
        savedPoi.setPoiId(poiId);
        savedPoi.setPoiName(createDto.getPoiName());
        savedPoi.setOrganizationId(createDto.getOrganizationId());
        savedPoi.setPoiType(createDto.getPoiType());
        savedPoi.setPoiCategory(createDto.getPoiCategory());
        savedPoi.setAddressCity(createDto.getAddressCity());
        savedPoi.setCreatedByUserId(userId);
        savedPoi.setIsActive(false);
        savedPoi.setStatus(com.poi.yow_point.application.model.PoiStatus.SUBMITTED);
        savedPoi.setLocationGeog(geometryFactory.createPoint(new Coordinate(createDto.getLongitude(), createDto.getLatitude())));

        // Mock check for existing name
        Mockito.when(pointOfInterestRepository.existsByNameAndOrganizationIdExcludingId(any(), any(), any()))
               .thenReturn(Mono.just(false));

        // Mock save operation
        Mockito.when(pointOfInterestRepository.save(any(PointOfInterest.class)))
               .thenReturn(Mono.just(savedPoi));

        // Mock findById for the entire lifecycle
        Mockito.when(pointOfInterestRepository.findById(poiId))
               .thenReturn(Mono.just(savedPoi), Mono.just(savedPoi), Mono.just(savedPoi), Mono.empty());

        // Mock deleteById
        Mockito.when(pointOfInterestRepository.deleteById(poiId)).thenReturn(Mono.empty());

        // Mock Redis Template
        org.springframework.data.redis.core.ReactiveValueOperations<String, PointOfInterestDTO> opsForValue = Mockito.mock(org.springframework.data.redis.core.ReactiveValueOperations.class);
        Mockito.when(redisTemplate.opsForValue()).thenReturn(opsForValue);
        Mockito.when(opsForValue.get(any())).thenReturn(Mono.empty());
        Mockito.when(opsForValue.set(any(), any(), any())).thenReturn(Mono.just(true));
        Mockito.when(opsForValue.delete(any())).thenReturn(Mono.just(true));

        // Mock Service Notifications
        Mockito.when(appUserService.getUserById(any())).thenReturn(Mono.just(new com.poi.yow_point.presentation.dto.AppUserDTO()));
        Mockito.when(notificationService.notifyPoiSubmitted(any(), any())).thenReturn(Mono.empty());

        // 1. Create POI
        PointOfInterestDTO createdPoi = webTestClient.post().uri("/api/pois")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PointOfInterestDTO.class)
                .returnResult().getResponseBody();

        assertThat(createdPoi).isNotNull();
        assertThat(createdPoi.getPoiId()).isEqualTo(poiId);
        assertThat(createdPoi.getPoiName()).isEqualTo("Test POI");
        assertThat(createdPoi.getIsActive()).isFalse();
        assertThat(createdPoi.getStatus()).isEqualTo(com.poi.yow_point.application.model.PoiStatus.SUBMITTED);

        // 2. Get POI by ID
        webTestClient.get().uri("/api/pois/{id}", poiId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.poi_name").isEqualTo("Test POI")
                .jsonPath("$.poi_id").isEqualTo(poiId.toString());

        // 3. Update POI
        com.poi.yow_point.presentation.dto.UpdatePoiDTO updateDto = com.poi.yow_point.presentation.dto.UpdatePoiDTO.builder()
                .poiName("Updated Test POI")
                .build();

        webTestClient.put().uri("/api/pois/{id}", poiId)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk();

        // 4. Delete POI
        webTestClient.delete().uri("/api/pois/{id}", poiId)
                .exchange()
                .expectStatus().isNoContent();

        // 5. Verify Deletion
        webTestClient.get().uri("/api/pois/{id}", poiId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
