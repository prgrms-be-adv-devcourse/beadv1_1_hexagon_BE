package com.example.searchservice.kafka;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.hexagon.core.events.commission.CommissionCreatedEvent;
import org.hexagon.core.events.commission.CommissionInitEvent;
import org.hexagon.core.vo.Commission;
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
                "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer"
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
    private CommissionRepository commissionRepository;

    @Test
    void kafka로_이벤트_주고_받기() throws ExecutionException, InterruptedException {
        // given
        String code = "test-001";

        CommissionCreatedEvent event = new CommissionCreatedEvent(
                code,
                "스프링 백엔드 개발자 구인",
                "상세 내용입니다.",
                "mem-001",
                "닉네임",
                List.of("Spring", "Backend"),
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(7),
                PaymentType.MONTHLY,
                50000L,
                false,
                Instant.now()
        );

        // when
        kafkaTemplate.send("commission-events", event).get();

        try {
            Thread.sleep(1000); // 5초 = 5000ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then
        CommissionDocumentEntity commissionDoc = commissionRepository.findById(code).get();
        System.out.println(commissionDoc);
    }

    @Test
    void kafka로_이벤트_주고_받기2() throws ExecutionException, InterruptedException {
        // given
        List<Commission> commissions = List.of(
                new Commission(
                        "test-002",
                        "스프링 백엔드 개발자 구인",
                        "상세 내용입니다.",
                        "mem-002",
                        "닉네임1",
                        List.of("Spring", "Backend"),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(7),
                        PaymentType.MONTHLY,
                        50000L,
                        false,
                        Instant.now()
                ),
                new Commission(
                        "test-003",
                        "파이썬 백엔드 개발자 구인",
                        "상세 내용입니다.",
                        "mem-003",
                        "닉네임2",
                        List.of("Spring", "Backend"),
                        LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(7),
                        PaymentType.MONTHLY,
                        50000L,
                        false,
                        Instant.now()
                )
        );

        CommissionInitEvent initEvent = new CommissionInitEvent(commissions);

        // when
        kafkaTemplate.send("commission-events", initEvent).get();

        try {
            Thread.sleep(1000); // 5초 = 5000ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then
        List<CommissionDocumentEntity> commissionDocs = new ArrayList<>();
        commissionRepository.findAll().forEach(commissionDocs::add);
        commissionDocs.forEach(System.out::println);
    }

}
