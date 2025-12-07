package com.example.cartpostservice.commissions.service.kafka;

import com.example.cartpostservice.commissions.service.kafka.dto.request.CommissionServiceMessage;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.commission.CommissionCreatedEvent;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommissionKafkaService {

    // KafkaTemplate 주입 (Config에서 빈으로 등록한 타입과 일치해야 함)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 전송할 토픽 이름 주입 (application.yml/properties 값)
    @Value("${search.topic.name}")
    private String searchTopicName;

    @Transactional
    public void createProducer(CommissionServiceMessage createMessage){

        CommissionCreatedEvent commissionCreatedEvent = new CommissionCreatedEvent(
                createMessage.code(),
                createMessage.title(),
                createMessage.content(),
                createMessage.memberCode(),
                createMessage.memberNickname(),
                createMessage.tags(),
                createMessage.startedAt(),
                createMessage.endedAt(),
                createMessage.paymentType(),
                createMessage.payAmount(),
                createMessage.isClosed(),
                createMessage.updatedAt()
        );

        kafkaTemplate.send(searchTopicName, commissionCreatedEvent);
    }

    public void updateProducer(CommissionServiceMessage updateMessage) {

        CommissionUpdatedEvent commissionUpdatedEvent = new CommissionUpdatedEvent(
                updateMessage.code(),
                updateMessage.title(),
                updateMessage.content(),
                updateMessage.memberCode(),
                updateMessage.memberNickname(),
                updateMessage.tags(),
                updateMessage.startedAt(),
                updateMessage.endedAt(),
                updateMessage.paymentType(),
                updateMessage.payAmount(),
                updateMessage.isClosed(),
                updateMessage.updatedAt()
        );

        kafkaTemplate.send(searchTopicName, commissionUpdatedEvent);
    }

    public void deleteProducer(String commissionCode) {
        CommissionDeletedEvent commissionDeletedEvent = new CommissionDeletedEvent(commissionCode);

        kafkaTemplate.send(searchTopicName, commissionDeletedEvent);
    }

    public void finishProducer(CommissionServiceMessage finishMessage) {

        CommissionUpdatedEvent commissionUpdatedEvent = new CommissionUpdatedEvent(
                finishMessage.code(),
                finishMessage.title(),
                finishMessage.content(),
                finishMessage.memberCode(),
                finishMessage.memberNickname(),
                finishMessage.tags(),
                finishMessage.startedAt(),
                finishMessage.endedAt(),
                finishMessage.paymentType(),
                finishMessage.payAmount(),
                finishMessage.isClosed(),
                finishMessage.updatedAt()
        );

        kafkaTemplate.send(searchTopicName, commissionUpdatedEvent);
    }
}
