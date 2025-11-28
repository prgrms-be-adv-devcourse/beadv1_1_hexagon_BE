package com.example.contractservice.deposit.service.event;

import com.example.contractservice.deposit.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = {
                "${kafka.consumer.topic.member.name}"
        }
)
@RequiredArgsConstructor
public class DepositKafkaHandler {
    private final DepositService depositService;

    @KafkaHandler
    public void handleMemberCreatedEvent(@Payload MemberCreatedEvent event) {
        depositService.createDeposit(event.memberCode());
    }
}
