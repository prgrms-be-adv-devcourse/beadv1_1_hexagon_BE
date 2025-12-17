package com.example.cartpostservice.commissions.infra.kafka.listener;

import com.example.cartpostservice.commissions.service.ContractEventToCommissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        topics = {
                "${kafka.topic.contract.name}"
        },
        groupId = "commission-service"
)
public class KafkaContractEventListener {

    private final ContractEventToCommissionService contractEventToCommissionService;

    @KafkaHandler
    public void handleEvent(@Payload ContractEvent contractEvent) {
        contractEventToCommissionService.updateToSyncData(contractEvent.commissionCode(), contractEvent.status());
    }
}
