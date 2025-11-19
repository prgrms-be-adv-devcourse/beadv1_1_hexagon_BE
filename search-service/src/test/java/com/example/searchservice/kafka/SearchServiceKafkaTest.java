package com.example.searchservice.kafka;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import com.example.searchservice.selfpromotion.repository.SelfPromotionRepository;
import com.example.searchservice.tag.repository.TagRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.awaitility.Awaitility;
import org.hexagon.core.events.commission.CommissionCreatedEvent;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionInitEvent;
import org.hexagon.core.events.commission.CommissionUpdatedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionCreatedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionDeletedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionInitEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpdatedEvent;
import org.hexagon.core.events.tag.TagInitEvent;
import org.hexagon.core.vo.Commission;
import org.hexagon.core.vo.PaymentType;
import org.hexagon.core.vo.SelfPromotion;
import org.hexagon.core.vo.Tag;
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
    private TagRepository tagRepository;

    @Autowired
    private SelfPromotionRepository selfPromotionRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Test
    void tagInitEventTest() throws ExecutionException, InterruptedException {
        // given
        List<Tag> tags = List.of(
                new Tag("test-001", "Spring"),
                new Tag("test-002", "React"),
                new Tag("test-003", "없는 기술명"));

        TagInitEvent tagInitEvent = new TagInitEvent(tags);

        // when
        kafkaTemplate.send("tag-events", tagInitEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(tagRepository.existsById("test-001")).isTrue();
                    assertThat(tagRepository.existsById("test-002")).isTrue();
                    assertThat(tagRepository.existsById("test-003")).isTrue();
                });

        // tagRepository.deleteAll();
    }

    @Test
    void selfPromotionInitEventTest() throws ExecutionException, InterruptedException {
        // given
        List<SelfPromotion> selfPromotions = List.of(
                new SelfPromotion(
                        "test-001",
                        "Spring 개발자 구직",
                        "Spring 개발자 구직 내용",
                        "mem-001",
                        "닉네임1",
                        PaymentType.MONTHLY,
                        3_000_000L,
                        Instant.now()
                ),
                new SelfPromotion(
                        "test-002",
                        "React 개발자 구직",
                        "React 개발자 구직 내용",
                        "mem-002",
                        "닉네임2",
                        PaymentType.MONTHLY,
                        3_000_000L,
                        Instant.now()
                )
        );

        SelfPromotionInitEvent selfPromotionInitEvent = new SelfPromotionInitEvent(selfPromotions);

        // when
        kafkaTemplate.send("selfpromotion-events", selfPromotionInitEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(selfPromotionRepository.existsById("test-001")).isTrue();
                    assertThat(selfPromotionRepository.existsById("test-002")).isTrue();
                });

    }

    @Test
    void selfPromotionCreatedEventTest() throws ExecutionException, InterruptedException {
        // given
        SelfPromotionCreatedEvent selfPromotionCreatedEvent =
                new SelfPromotionCreatedEvent(
                        "test-003",
                        "Kotlin 개발자 구직",
                        "Kotlin 개발자 구직 내용",
                        "mem-003",
                        "닉네임3",
                        PaymentType.MONTHLY,
                        3_000_000L,
                        Instant.now()
                );

        // when
        kafkaTemplate.send("selfpromotion-events", selfPromotionCreatedEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(selfPromotionRepository.existsById("test-003")).isTrue();
                });
    }

    @Test
    void selfPromotionUpdatedEventTest() throws ExecutionException, InterruptedException {
        // given
        SelfPromotionUpdatedEvent selfPromotionUpdatedEvent =
                new SelfPromotionUpdatedEvent(
                        "test-003",
                        "Python 개발자 구직",
                        "Python 개발자 구직 내용",
                        "mem-003",
                        "닉네임3",
                        PaymentType.MONTHLY,
                        2_500_000L,
                        Instant.now()
                );

        // when
        kafkaTemplate.send("selfpromotion-events", selfPromotionUpdatedEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(selfPromotionRepository.findById("test-003").get().getTitle()).isEqualTo("Python 개발자 구직");
                });
    }

    @Test
    void selfPromotionDeletedEventTest() throws ExecutionException, InterruptedException {
        // given
        SelfPromotionDeletedEvent selfPromotionDeletedEvent =
                new SelfPromotionDeletedEvent("test-001");

        // when
        kafkaTemplate.send("selfpromotion-events", selfPromotionDeletedEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(selfPromotionRepository.existsById("test-001")).isFalse();
                });
    }

    @Test
    void commissionInitEvent() throws ExecutionException, InterruptedException {
        // given
        List<Commission> commissions = List.of(
                new Commission(
                        "test-001",
                        "Spring 개발자 구인",
                        "Spring 개발자 구인 내용",
                        "mem-001",
                        "닉네임1",
                        List.of("Spring", "Backend"),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(7),
                        PaymentType.MONTHLY,
                        2_000_000L,
                        false,
                        Instant.now()
                ),
                new Commission(
                        "test-002",
                        "React 개발자 구인",
                        "React 개발자 구인 내용",
                        "mem-002",
                        "닉네임2",
                        List.of("React", "FrontEnd"),
                        LocalDate.now(),
                        LocalDate.now(),
                        PaymentType.MONTHLY,
                        2_000_000L,
                        false,
                        Instant.now()
                )
        );

        CommissionInitEvent initEvent = new CommissionInitEvent(commissions);

        // when
        kafkaTemplate.send("commission-events", initEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(commissionRepository.existsById("test-001")).isTrue();
                    assertThat(commissionRepository.existsById("test-002")).isTrue();
                });
    }

    @Test
    void commissionCreatedEventTest() throws ExecutionException, InterruptedException {
        // given
        CommissionCreatedEvent createdEvent = new CommissionCreatedEvent(
                "test-003",
                "Kotlin 개발자 구인",
                "Kotlin 개발자 구인 내용",
                "mem-003",
                "닉네임3",
                List.of("Kotlin"),
                LocalDate.now(),
                LocalDate.now(),
                PaymentType.MONTHLY,
                2_000_000L,
                false,
                Instant.now()
        );

        // when
        kafkaTemplate.send("commission-events", createdEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(commissionRepository.existsById("test-003")).isTrue()
                );
    }

    @Test
    void commissionUpdatedEventTest() throws ExecutionException, InterruptedException {
        // given
        CommissionUpdatedEvent updatedEvent = new CommissionUpdatedEvent(
                "test-003",
                "Python 개발자 구인",
                "Python 개발자 구인 내용",
                "mem-003",
                "닉네임3",
                List.of("Kotlin"),
                LocalDate.now(),
                LocalDate.now(),
                PaymentType.MONTHLY,
                2_500_000L,
                false,
                Instant.now()
        );

        // when
        kafkaTemplate.send("commission-events", updatedEvent).get();

        // then
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(commissionRepository.findById("test-003").get().getTitle()).isEqualTo("Python 개발자 구인");
                });
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
