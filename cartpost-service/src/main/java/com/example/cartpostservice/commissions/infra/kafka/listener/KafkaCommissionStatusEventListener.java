package com.example.cartpostservice.commissions.infra.kafka.listener;

import com.example.cartpostservice.commissions.service.CommissionStatusEventToCommissionService;
import com.example.cartpostservice.commissions.service.DomainCompositeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.CommissionOpenCloseEvent;
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
                "${kafka.topic.commission.status.name}"
        },
        groupId = "${spring.kafka.consumer.group-id}"
)
public class KafkaCommissionStatusEventListener {

    private final CommissionStatusEventToCommissionService commissionStatusEventToCommissionService;

    @KafkaHandler
    public void handleEvent(@Payload CommissionOpenCloseEvent commissionOpenCloseEvent) {
        commissionStatusEventToCommissionService.changeCommissionStatus(commissionOpenCloseEvent.commissionCode(),
                commissionOpenCloseEvent.isOpen());
    }
}
