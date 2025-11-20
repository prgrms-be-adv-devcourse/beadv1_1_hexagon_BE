package com.example.profileservice.common.model.vo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Key를 받는 오버로딩 메서드
    public void send(String topic, String key, Object event) {
        log.info("Sending event to topic: {} with key: {} and data: {}", topic, key, event);

        // Key와 함께 메시지 전송
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Event sent successfully. Topic: {}, Partition: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(), // 파티션 정보 로그 추가
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send event to topic: {}. Error: {}", topic, ex.getMessage());
                    }
                });
    }

    // 기존 Key가 null인 메서드는 초기 동기화 용도로 유지 가능
    public void send(String topic, Object event) {
        // null 키를 사용하는 경우 메시지 순서가 보장되지 않음을 인지
        send(topic, null, event);
    }
}
