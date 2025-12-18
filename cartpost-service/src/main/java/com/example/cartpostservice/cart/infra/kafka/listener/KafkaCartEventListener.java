package com.example.cartpostservice.cart.infra.kafka.listener;

import com.example.cartpostservice.cart.service.CartPostKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.ContractEvent;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
// 클래스 레벨 리스너: 여러 토픽을 구독합니다.
@KafkaListener(
        topics = {
                "${kafka.topic.member.create.name}",   // Member 서버 이벤트 토픽
                "${kafka.topic.contract.name}"  // Contract 서버 이벤트 토픽
        },
        groupId = "${spring.kafka.consumer.group-id}" // 컨슈머 그룹 ID
)
public class KafkaCartEventListener {

    private final CartPostKafkaService cartPostKafkaService;


    /**
     * 1. 장바구니 생성 (K) Member 서버에서 "사용자 생성" 메세지가 오면 실행 Payload: MemberCreatedEvent
     */
    @KafkaHandler
    public void handleEvent(@Payload MemberCreatedEvent event) {
        cartPostKafkaService.createCart(event.memberCode());
    }

    /**
     * 2. 장바구니 아이템 추가 (K) Contract 서버에서 "계약 CONFIRMED" 메세지가 오면 실행 Payload: ContractConfirmedEvent
     */
    @KafkaHandler
    public void handleEvent(@Payload ContractEvent event) {

        cartPostKafkaService.addCartItem(event);

    }

}
