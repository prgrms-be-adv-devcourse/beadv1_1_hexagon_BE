package com.example.profileservice.common.model.vo.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractDoneConsumer {

    private final CompletedContractStore completedContractStore;

    @KafkaListener(
            topics = "${kafka.topic.contract}",
            groupId = "rating-service"
    )
    public void consume(ContractEvent event) {

        if (event.status() != ContractStatus.DONE) {
            return;
        }

        completedContractStore.markCompleted(event.contractCode());
        log.info("[Rating] Contract DONE received: {}", event.contractCode());
    }
}
