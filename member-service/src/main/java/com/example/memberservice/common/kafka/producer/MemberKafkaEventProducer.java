package com.example.memberservice.common.kafka.producer;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.ContractEvent;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.hexagon.core.events.member.MemberDeletedClientRoleEvent;
import org.hexagon.core.events.member.MemberDeletedEvent;
import org.hexagon.core.events.member.MemberDeletedFreelancerRoleEvent;
import org.hexagon.core.events.member.MemberUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
@Slf4j
public class MemberKafkaEventProducer implements MemberEventProducer{

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.member.create-topic}")
    private String memberCreatedTopicName;

    @Value("${kafka.topic.member.update-topic}")
    private String memberUpdatedTopicName;

    @Value("${kafka.topic.member.delete-topic}")
    private String memberDeletedTopicName;

    @Value("${kafka.topic.member.delete-freelancer-role-topic}")
    private String memberFreelancerRoleDeletedTopicName;

    @Value("${kafka.topic.member.delete-client-role-topic}")
    private String memberClientRoleDeletedTopicName;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendCreatedEvent(MemberCreatedEvent event) {
        return kafkaTemplate.send(memberCreatedTopicName, event.memberCode(), event);
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendUpdatedEvent(MemberUpdatedEvent event) {
        return kafkaTemplate.send(memberUpdatedTopicName, event.memberCode(), event);
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendDeletedEvent(
        MemberDeletedEvent event) {
        return kafkaTemplate.send(memberDeletedTopicName, event.memberCode(), event);
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendDeletedClientRoleEvent(
        MemberDeletedClientRoleEvent event) {
        return kafkaTemplate.send(memberClientRoleDeletedTopicName, event.memberCode(), event);
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendDeletedFreelancerRoleEvent(
        MemberDeletedFreelancerRoleEvent event) {
        return kafkaTemplate.send(memberFreelancerRoleDeletedTopicName, event.memberCode(), event);
    }
}
