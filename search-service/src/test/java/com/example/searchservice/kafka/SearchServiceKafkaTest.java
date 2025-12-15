package com.example.searchservice.kafka;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import com.example.searchservice.tag.repository.TagRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.hexagon.core.events.commission.CommissionUpsertEvent;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpsertEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionDeletedEvent;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest(
        properties = {
                // EmbeddedKafka 브로커를 사용하도록 강제
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",

                // Consumer 설정
                "spring.kafka.consumer.group-id=search-service-group",
                "spring.kafka.consumer.auto-offset-reset=earliest",
                "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
                "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
                "spring.kafka.consumer.properties.spring.json.trusted.packages=org.hexagon.core.events.*",
                "spring.kafka.consumer.properties.spring.json.use.type.headers=true",

                // Producer 설정
                "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
                "spring.kafka.producer.acks=all"
        }
)
@EmbeddedKafka(
        partitions = 1,
        topics = {
                "tag-events",
                "selfpromotion-events",
                "commission-events"
        }
)
class SearchServiceKafkaTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SelfPromotionRepository selfPromotionRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Test
    void selfPromotionUpsertEventTest() throws ExecutionException, InterruptedException {
        // given
        SelfPromotionUpsertEvent upsertEvent =
                new SelfPromotionUpsertEvent(
                        "test-001",
                        "Kotlin 개발자 구직",
                        "Kotlin 개발자 구직 내용",
                        "mem-001",
                        "닉네임1",
                        PaymentType.MONTHLY,
                        3_000_000L,
                        Instant.now()
                );

        // when
        kafkaTemplate.send("selfpromotion-events", upsertEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(selfPromotionRepository.existsById("test-001")).isTrue();
                });
    }

    @Test
    void selfPromotionDeletedEventTest() throws ExecutionException, InterruptedException {
        // given
        SelfPromotionDeletedEvent deletedEvent =
                new SelfPromotionDeletedEvent("test-001");

        // when
        kafkaTemplate.send("selfpromotion-events", deletedEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(selfPromotionRepository.existsById("test-001")).isFalse();
                });
    }

    @Test
    void commissionUpsertEventTest() throws ExecutionException, InterruptedException {
        // given
        CommissionUpsertEvent upsertEvent = new CommissionUpsertEvent(
                "test-001",
                "Kotlin 개발자 구인",
                "Kotlin 개발자 구인 내용",
                "mem-001",
                "닉네임1",
                List.of("Kotlin"),
                LocalDate.now(),
                LocalDate.now(),
                PaymentType.MONTHLY,
                2_000_000L,
                false,
                Instant.now()
        );

        // when
        kafkaTemplate.send("commission-events", upsertEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(commissionRepository.existsById("test-001")).isTrue()
                );
    }

    @Test
    void commissionDeletedEventTest() throws ExecutionException, InterruptedException {
        // given
        CommissionDeletedEvent deletedEvent = new CommissionDeletedEvent("test-001");

        // when
        kafkaTemplate.send("commission-events", deletedEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(commissionRepository.existsById("test-001")).isFalse();
                });
    }
}
