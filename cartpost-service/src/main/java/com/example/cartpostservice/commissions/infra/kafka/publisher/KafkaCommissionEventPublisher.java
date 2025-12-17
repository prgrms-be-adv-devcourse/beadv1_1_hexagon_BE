package com.example.cartpostservice.commissions.infra.kafka.publisher;

import com.example.cartpostservice.commissions.service.kafka.dto.request.CommissionServiceMessage;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionUpsertEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class KafkaCommissionEventPublisher {

    // KafkaTemplate 주입 (Config에서 빈으로 등록한 타입과 일치해야 함)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 전송할 토픽 이름 주입 (application.yml/properties 값)
    @Value("${kafka.topic.commission.name}")
    private String commissionStatusTopic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void upsertProducer(CommissionUpsertEvent commissionUpsertEvent) {

        kafkaTemplate.send(commissionStatusTopic, commissionUpsertEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteProducer(CommissionDeletedEvent commissionDeletedEvent) {
        kafkaTemplate.send(commissionStatusTopic, commissionDeletedEvent);
    }
}
