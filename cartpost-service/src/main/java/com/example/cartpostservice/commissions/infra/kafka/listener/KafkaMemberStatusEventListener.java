package com.example.cartpostservice.commissions.infra.kafka.listener;

import com.example.cartpostservice.commissions.service.MemberEventToCommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.member.MemberDeletedClientRoleEvent;
import org.hexagon.core.events.member.MemberDeletedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        topics = {
                "${kafka.topic.member.role.name}",
                "${kafka.topic.member.cancel.name}"
        },
        groupId = "${spring.kafka.consumer.group-id}"
)
public class KafkaMemberStatusEventListener {

    private final MemberEventToCommissionService eventToCommissionService;

    @KafkaHandler
    public void handleEvent(@Payload MemberDeletedClientRoleEvent memberDeletedClientRoleEvent) {
        eventToCommissionService.stopCommissions(memberDeletedClientRoleEvent.memberCode());
    }

    @KafkaHandler
    public void handleEvent(@Payload MemberDeletedEvent memberDeletedEvent) {
        eventToCommissionService.deleteCommissions(memberDeletedEvent.memberCode());
    }

}
