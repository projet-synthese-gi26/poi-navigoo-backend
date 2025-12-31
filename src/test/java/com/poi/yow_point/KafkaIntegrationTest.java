package com.poi.yow_point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

import com.poi.yow_point.application.services.point_of_interest.PointOfInterestService;
import com.poi.yow_point.infrastructure.entities.PointOfInterest;
import com.poi.yow_point.infrastructure.repositories.PointOfInterest.PointOfInterestRepository;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;

import reactor.core.publisher.Mono;

@SpringBootTest(
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
        }
)
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = "poi-created")
@ActiveProfiles("test")
class KafkaIntegrationTest {

    @Autowired
    private PointOfInterestService pointOfInterestService;

    @MockBean
    private PointOfInterestRepository pointOfInterestRepository;

    @MockBean
    private com.poi.yow_point.infrastructure.repositories.poiDocument.PoiDocumentRepository poiDocumentRepository;

    @Autowired
    private TestConsumer testConsumer;

    @TestConfiguration
    static class KafkaTestConsumerConfiguration {

        @Bean
        public TestConsumer testConsumer() {
            return new TestConsumer();
        }
    }

    @Test
    void whenCreatePoi_thenMessageSentToKafka() throws Exception {

        PointOfInterestDTO poiDto = new PointOfInterestDTO();
        poiDto.setPoiName("Test POI From Test");
        poiDto.setOrganizationId(UUID.randomUUID());
        poiDto.setPoiCategory(com.poi.yow_point.application.model.PoiCategory.FOOD_DRINK);
        poiDto.setPoiType(com.poi.yow_point.application.model.PoiType.RESTAURANT);
        poiDto.setAddressStreetName("Test Address");
        poiDto.setAddressCity("Test City");
        poiDto.setAddressCountry("Test Country");
        poiDto.setLatitude(0.0);
        poiDto.setLongitude(0.0);

        PointOfInterest savedEntity = new PointOfInterest();
        savedEntity.setPoiId(UUID.randomUUID());
        savedEntity.setPoiName(poiDto.getPoiName());
        savedEntity.setOrganizationId(poiDto.getOrganizationId());

        when(pointOfInterestRepository.existsByNameAndOrganizationIdExcludingId(any(), any(), any()))
                .thenReturn(Mono.just(false));

        when(pointOfInterestRepository.save(any(PointOfInterest.class)))
                .thenReturn(Mono.just(savedEntity));

        pointOfInterestService.createPoi(poiDto).block();

        boolean messageConsumed = testConsumer.getLatch().await(10, TimeUnit.SECONDS);

        assertThat(messageConsumed).isTrue();
        assertThat(testConsumer.getPayload()).isNotNull();
        assertThat(testConsumer.getPayload().getPoiName()).isEqualTo("Test POI From Test");
    }

    public static class TestConsumer {

        private final CountDownLatch latch = new CountDownLatch(1);
        private PointOfInterestDTO payload;

        @KafkaListener(topics = "poi-created", groupId = "test-group")
        public void receive(PointOfInterestDTO payload) {
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
