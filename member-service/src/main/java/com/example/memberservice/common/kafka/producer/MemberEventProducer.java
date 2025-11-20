package com.example.memberservice.common.kafka.producer;

import java.util.concurrent.CompletableFuture;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.hexagon.core.events.member.MemberUpdatedEvent;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public interface MemberEventProducer {
    CompletableFuture<SendResult<String, Object>> sendCreatedEvent(MemberCreatedEvent event);
    CompletableFuture<SendResult<String, Object>> sendUpdatedEvent(MemberUpdatedEvent event);
}
