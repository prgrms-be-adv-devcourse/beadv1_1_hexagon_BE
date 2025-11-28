package com.example.cartpostservice.commissions.integration;

import com.example.cartpostservice.commissions.controller.dto.request.CommissionUpsertRequest;
import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.dto.response.TagServiceResult;
import com.example.cartpostservice.commissions.service.kafka.CommissionKafkaService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hexagon.core.events.commission.CommissionCreatedEvent;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionUpdatedEvent;
import org.hexagon.core.vo.PaymentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {"${search.topic.name}"}
)
@TestPropertySource(properties = {
        "search.topic.name=test-only-kafka-service",
        "spring.kafka.consumer.auto-offset-reset=earliest",

        // 랜덤으로 생성된 브로커 주소를 Producer에게 주입
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",

        // 더미 설정
        "member.topic.name=dummy-member-topic",
        "contract.topic.name=dummy-contract-topic",
        "spring.kafka.consumer.group-id=test-group-dummy",

        // DB 관련 설정 (data.sql 방지)
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
// DirtiesContext를 각 메소드마다가 아니라 클래스 단위로 적용하여 안정성 확보
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommissionsKafkaServiceTest {

    @Autowired
    private CommissionKafkaService commissionKafkaService;

    @Autowired
    private CommissionsRepository commissionsRepository;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${search.topic.name}")
    private String topicName;

    private BlockingQueue<ConsumerRecord<String, Object>> records;
    private KafkaMessageListenerContainer<String, Object> container;

    @BeforeEach
    void setUp() {
        commissionsRepository.deleteAll();
        records = new LinkedBlockingQueue<>();

        // AdminClient로 토픽 생성 확인
        try (AdminClient adminClient = AdminClient.create(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString()))) {
            // 토픽이 없으면 생성 시도 (이미 있으면 Exception 무시)
            adminClient.createTopics(Collections.singleton(new NewTopic(topicName, 1, (short) 1)));
        } catch (Exception e) {
            // Ignore if topic exists
        }

        // Consumer 설정
        Map<String, Object> configs = new java.util.HashMap<>(
                org.springframework.kafka.test.utils.KafkaTestUtils.consumerProps("test-group", "false", embeddedKafkaBroker));

        // 랜덤 포트 주소를 동적으로 가져와서 설정
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis()); // 유니크 그룹 ID
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("*");

        DefaultKafkaConsumerFactory<String, Object> consumerFactory =
                new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), deserializer);

        ContainerProperties containerProperties = new ContainerProperties(topicName);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, Object>) records::add);
        container.start();

        // 할당 대기
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    @DisplayName("createProducer 호출 시 DB 정보를 조회해 Kafka 메시지를 발행한다")
    void createProducerTest() throws InterruptedException {
        // given
        String commissionCode = "COM_CREATE_001";
        saveDummyEntity(commissionCode);

        CommissionUpsertRequest request = new CommissionUpsertRequest(
                "Request Title", "Request Content", PaymentType.MONTHLY, "10000",
                LocalDate.now(), LocalDate.now().plusDays(7), List.of("Tag1")
        );

        // when
        commissionKafkaService.createProducer(commissionCode, request);

        // then
        ConsumerRecord<String, Object> record = records.poll(10, TimeUnit.SECONDS);
        assertThat(record).isNotNull();
        assertThat(record.value()).isInstanceOf(CommissionCreatedEvent.class);

        CommissionCreatedEvent event = (CommissionCreatedEvent) record.value();
        assertThat(event.code()).isEqualTo(commissionCode);
        assertThat(event.tags()).contains("Tag1");
    }

    @Test
    @DisplayName("updateProducer 호출 시 Kafka 메시지(UpdatedEvent)를 발행한다")
    void updateProducerTest() throws InterruptedException {
        // given
        String commissionCode = "COM_UPDATE_001";
        saveDummyEntity(commissionCode);

        CommissionUpsertRequest request = new CommissionUpsertRequest(
                "Updated Title", "Content", PaymentType.MONTHLY, "20000",
                LocalDate.now(), LocalDate.now().plusDays(7), List.of("TagUpdated")
        );

        // when
        commissionKafkaService.updateProducer(commissionCode, request);

        // then
        ConsumerRecord<String, Object> record = records.poll(10, TimeUnit.SECONDS);
        assertThat(record).isNotNull();
        assertThat(record.value()).isInstanceOf(CommissionUpdatedEvent.class);

        CommissionUpdatedEvent event = (CommissionUpdatedEvent) record.value();
        assertThat(event.code()).isEqualTo(commissionCode);
        assertThat(event.tags()).contains("TagUpdated");
    }

    @Test
    @DisplayName("deleteProducer 호출 시 Kafka 메시지(DeletedEvent)를 발행한다")
    void deleteProducerTest() throws InterruptedException {
        // given
        String commissionCode = "COM_DELETE_001";

        // when
        commissionKafkaService.deleteProducer(commissionCode);

        // then
        ConsumerRecord<String, Object> record = records.poll(10, TimeUnit.SECONDS);
        assertThat(record).isNotNull();
        assertThat(record.value()).isInstanceOf(CommissionDeletedEvent.class);

        CommissionDeletedEvent event = (CommissionDeletedEvent) record.value();
        assertThat(event.code()).isEqualTo(commissionCode);
    }

    @Test
    @DisplayName("finishProducer 호출 시 Tag 정보를 포함하여 Kafka 메시지를 발행한다")
    void finishProducerTest() throws InterruptedException {
        // given
        String commissionCode = "COM_FINISH_001";
        saveDummyEntity(commissionCode);

        TagServiceResult tagResult = new TagServiceResult(commissionCode, List.of("FinishedTag"));

        // when
        commissionKafkaService.finishProducer(commissionCode, tagResult);

        // then
        ConsumerRecord<String, Object> record = records.poll(10, TimeUnit.SECONDS);
        assertThat(record).isNotNull();
        assertThat(record.value()).isInstanceOf(CommissionUpdatedEvent.class);

        CommissionUpdatedEvent event = (CommissionUpdatedEvent) record.value();
        assertThat(event.tags()).contains("FinishedTag");
    }

    private void saveDummyEntity(String code) {
        CommissionsEntity entity = CommissionsEntity.builder()
                .memberCode("MEM_001")
                .writerName("Writer")
                .title("DB Title")
                .content("DB Content")
                .paymentType(PaymentType.MONTHLY)
                .unitAmount("10000")
                .startedAt(LocalDate.now())
                .endedAt(LocalDate.now())
                .build();

        ReflectionTestUtils.setField(entity, "code", code);
        //ReflectionTestUtils.setField(entity, "id", 1L); // ID 강제 주입

        commissionsRepository.save(entity);
    }
}