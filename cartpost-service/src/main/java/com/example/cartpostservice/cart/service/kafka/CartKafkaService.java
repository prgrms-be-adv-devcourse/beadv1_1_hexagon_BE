package com.example.cartpostservice.cart.service.kafka;

import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartKafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.commission.name}")
    private String targetTopicName;

    public void deleteProducer(String contractCode) {
        // 카프카 서버로 삭제 이벤트 전달
        CommissionDeletedEvent event = new CommissionDeletedEvent(contractCode);

        // kafkaTemplate.send(토픽이름, 메세지객체)
        kafkaTemplate.send(targetTopicName, event);
    }
}
