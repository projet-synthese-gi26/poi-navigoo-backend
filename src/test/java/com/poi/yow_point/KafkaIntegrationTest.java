package com.poi.yow_point;

import com.poi.yow_point.application.services.point_of_interest.PointOfInterestService;
import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.infrastructure.repositories.PointOfInterest.PointOfInterestRepository;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import com.poi.yow_point.presentation.dto.CreatePoiDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = { "poi-created" })
@ActiveProfiles("test")
class KafkaIntegrationTest {

    @Autowired
    private PointOfInterestService pointOfInterestService;

    @MockBean
    private PointOfInterestRepository pointOfInterestRepository;

    @MockBean
    private org.springframework.data.redis.core.ReactiveRedisTemplate<String, PointOfInterestDTO> redisTemplate;

    @MockBean
    private com.poi.yow_point.infrastructure.kafka.KafkaConsumerService kafkaConsumerService;

    @MockBean
    private com.poi.yow_point.infrastructure.kafka.KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setupRedisMock() {
        org.springframework.data.redis.core.ReactiveValueOperations<String, PointOfInterestDTO> opsForValue = org.mockito.Mockito.mock(org.springframework.data.redis.core.ReactiveValueOperations.class);
        org.mockito.Mockito.when(redisTemplate.opsForValue()).thenReturn(opsForValue);
        org.mockito.Mockito.when(opsForValue.get(org.mockito.ArgumentMatchers.anyString())).thenReturn(reactor.core.publisher.Mono.empty());
        org.mockito.Mockito.when(opsForValue.set(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(reactor.core.publisher.Mono.just(true));
        org.mockito.Mockito.when(opsForValue.delete(org.mockito.ArgumentMatchers.anyString())).thenReturn(reactor.core.publisher.Mono.just(true));
    }

    @TestConfiguration
    static class KafkaTestConsumerConfiguration {
        @Bean
        public TestConsumer testConsumer() {
            return new TestConsumer();
        }
    }

    @MockBean
    private com.poi.yow_point.application.services.appUser.AppUserService appUserService;

    @MockBean
    private com.poi.yow_point.application.services.notification.NotificationService notificationService;

    @Test
    void whenCreatePoi_thenMessageSentToKafka() throws Exception {
        // Given
        CreatePoiDTO createDto = new CreatePoiDTO();
        createDto.setPoiName("Test POI From Test");
        createDto.setOrganizationId(UUID.randomUUID());
        createDto.setCreatedByUserId(UUID.randomUUID());
        createDto.setPoiCategory(com.poi.yow_point.application.model.PoiCategory.FOOD_DRINK);
        createDto.setPoiType(com.poi.yow_point.application.model.PoiType.RESTAURANT);
        createDto.setLatitude(0.0);
        createDto.setLongitude(0.0);

        PointOfInterest savedEntity = new PointOfInterest();
        savedEntity.setPoiId(UUID.randomUUID());
        savedEntity.setPoiName(createDto.getPoiName());
        savedEntity.setOrganizationId(createDto.getOrganizationId());
        savedEntity.setCreatedByUserId(createDto.getCreatedByUserId());

        when(pointOfInterestRepository.existsByNameAndOrganizationIdExcludingId(any(), any(), any())).thenReturn(Mono.just(false));
        when(pointOfInterestRepository.save(any(PointOfInterest.class))).thenReturn(Mono.just(savedEntity));
        when(appUserService.getUserById(any())).thenReturn(Mono.just(new com.poi.yow_point.presentation.dto.AppUserDTO()));
        when(notificationService.notifyPoiSubmitted(any(), any())).thenReturn(Mono.empty());

        // When
        pointOfInterestService.createPoi(createDto).block();

        // Then
        org.mockito.Mockito.verify(kafkaProducerService).sendMessage(org.mockito.ArgumentMatchers.eq("poi-created"), org.mockito.ArgumentMatchers.any(PointOfInterestDTO.class));
    }

    public static class TestConsumer {
        private final CountDownLatch latch = new CountDownLatch(1);
        private PointOfInterestDTO payload;

        @KafkaListener(topics = "poi-created", groupId = "test-group")
        public void receive(PointOfInterestDTO payload) {
            System.out.println("TestConsumer received: " + payload);
            this.payload = payload;
            latch.countDown();
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public PointOfInterestDTO getPayload() {
            return payload;
        }
    }
}
