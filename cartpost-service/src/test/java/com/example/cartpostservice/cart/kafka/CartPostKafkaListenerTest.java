package com.example.cartpostservice.cart.kafka;


import com.example.cartpostservice.cart.controller.dto.response.ContractBriefWithNicknameResponse;
import com.example.cartpostservice.cart.controller.internal.ContractClient;
import com.example.cartpostservice.cart.model.CartItemsEntity;
import com.example.cartpostservice.cart.model.CartsEntity;
import com.example.cartpostservice.cart.repository.CartItemsRepository;
import com.example.cartpostservice.cart.repository.CartsRepository;
import com.example.cartpostservice.common.dto.ResponseDto;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.hexagon.core.events.contract.ContractEvent;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
// Spring Boot 3.4+ (이하 버전은 @MockBean 사용)
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        // 성공 사례처럼 변수(${...})를 사용
        topics = { "${member.topic.name}", "${contract.topic.name}" }
)
@TestPropertySource(properties = {
        // 1. [핵심] 위에서 사용한 토픽 변수의 값을 여기서 정의 (성공 코드 패턴 적용)
        "member.topic.name=test-member-topic",
        "contract.topic.name=test-contract-topic",

        // 2. 랜덤 포트 주입 (성공 코드와 동일)
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",

        // 3. 컨슈머 그룹 및 오프셋 설정 (성공 코드 패턴 적용)
        "spring.kafka.consumer.group-id=test-group-cartpost",
        "spring.kafka.consumer.auto-offset-reset=earliest",

        // 4. 직렬화/역직렬화 설정 (필수 유지)
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",

        // 5. DB 설정 (성공 코드와 동일)
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CartPostKafkaListenerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // ▼▼▼ 기존 코드에 있는 의존성들을 Mocking 합니다 ▼▼▼

    @MockitoBean
    private CartsRepository cartsRepository;

    @MockitoBean
    private CartItemsRepository cartItemsRepository;

    @MockitoBean
    private ContractClient contractClient;

    @Test
    @DisplayName("MemberCreatedEvent 수신 시 장바구니(CartsEntity)가 저장되어야 한다")
    void handleMemberCreatedEventTest() {
        // given
        String memberCode = "MEM_001";
        MemberCreatedEvent event = new MemberCreatedEvent(memberCode);

        // DB에 해당 멤버의 장바구니가 없다고 가정 (existsByMemberCode -> false)
        given(cartsRepository.existsByMemberCode(memberCode)).willReturn(false);

        // when
        kafkaTemplate.send("test-member-topic", event);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // cartsRepository.save()가 호출되었는지 확인
            verify(cartsRepository, times(1)).save(any(CartsEntity.class));
        });
    }

    @Test
    @DisplayName("ContractEvent(CONFIRMED) 수신 시 아이템(CartItemsEntity)이 저장되어야 한다")
    void handleContractConfirmedEventTest() {
        // given
        String memberCode = "MEM_002";
        String contractCode = "CONT_123";

        // 1. 이벤트 객체 생성
        ContractEvent event = new ContractEvent(
                memberCode, // 순서 주의 (Record 정의에 따라 다를 수 있음)
                contractCode,
                java.time.Instant.now(),
                "CONFIRMED"
        );

        // 2. Mocking: 장바구니가 이미 존재한다고 가정
        CartsEntity mockCart = CartsEntity.builder().memberCode(memberCode).build();
        // (주의: Entity에 getCode() 호출 시 null이 아니어야 한다면 ReflectionTestUtils로 ID 주입 필요)

        given(cartsRepository.findByMemberCode(memberCode))
                .willReturn(Optional.of(mockCart));

        // 3. Mocking: 이미 담긴 아이템이 아니라고 가정
        given(cartItemsRepository.existsByContractCode(contractCode))
                .willReturn(false);

        // 4. Mocking: 외부 Feign Client (ContractClient) 응답 설정
        ContractBriefWithNicknameResponse briefInfo = new ContractBriefWithNicknameResponse(
                contractCode, "requestorName", "contractorName", Instant.now(), Instant.now().plus(3, ChronoUnit.DAYS), "MONTHLY", 10000L, "홍길동과 JohnDoe의 계약"
        );
        ResponseDto<List<ContractBriefWithNicknameResponse>> responseDto =
                ResponseDto.success(CustomStatusCode.SUCCESS, List.of(briefInfo));

        given(contractClient.getBriefInfo(anyList()))
                .willReturn(responseDto);

        // when
        kafkaTemplate.send("test-contract-topic", event);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            // Feign Client가 호출되었는지 확인
            verify(contractClient, times(1)).getBriefInfo(anyList());

            // 최종적으로 아이템이 DB에 저장(save)되었는지 확인
            verify(cartItemsRepository, times(1)).save(any(CartItemsEntity.class));
        });
    }
}