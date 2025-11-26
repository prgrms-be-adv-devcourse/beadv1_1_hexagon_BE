package com.example.memberservice.common.kafka.producer;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.ContractEvent;
import org.hexagon.core.events.member.MemberCreatedEvent;
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



    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendCreatedEvent(MemberCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(memberCreatedTopicName,
            event.memberCode(), event);


        send.whenComplete((result, ex) -> {
            if (ex != null) {
                // 전송 실패
                System.err.println("Kafka 메시지 전송 실패: " + ex.getMessage());
            } else {
                // 전송 성공
                System.out.println("Kafka 메시지 전송 성공: " + result.getRecordMetadata());
            }
        });

        return send;
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public CompletableFuture<SendResult<String, Object>> sendUpdatedEvent(MemberUpdatedEvent event) {
        return kafkaTemplate.send(memberUpdatedTopicName, event.memberCode(), event);
    }
}
